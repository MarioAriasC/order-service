package com.polarbookshop.orderservice.order.domain

import com.polarbookshop.orderservice.book.Book
import com.polarbookshop.orderservice.book.BookClient
import com.polarbookshop.orderservice.order.event.OrderAcceptedMessage
import com.polarbookshop.orderservice.order.event.OrderDispatchedMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mu.KotlinLogging
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class OrderService(
    private val repository: OrderRepository,
    private val client: BookClient,
    private val bridge: StreamBridge
) {
    fun getAllOrders(): Flow<Order> = repository.findAll()

    @Transactional
    suspend fun submitOrder(isbn: String, quantity: Int): Order {
        val book = client.get(isbn)
        val order = if (book != null) {
            buildAcceptedOrder(book, quantity)
        } else {
            buildRejectedOrder(isbn, quantity)
        }
        return repository.save(order)
            .also(::publishOrderAcceptedEvent)
    }

    private  fun publishOrderAcceptedEvent(order: Order) {
        if (order.status != OrderStatus.ACCEPTED) return

        val message = OrderAcceptedMessage(order.id!!)
        logger.info { "Sending order accepted event with id ${order.id}" }
        val result = bridge.send(OUT, message)
        logger.info { "Result of sending data for order with id ${order.id}: $result" }
    }

    suspend fun consumeOrderDispatchEvent(flow: Flow<OrderDispatchedMessage>): Flow<Order> = flow
        .map { message -> repository.findById(message.orderId)!! }
        .map(::buildDispatchedOrder)
        .map(repository::save)

    companion object {

        private val logger = KotlinLogging.logger {}

        const val OUT = "acceptOrder-out-0"

        fun buildRejectedOrder(bookIsbn: String, quantity: Int) =
            Order(bookIsbn, null, null, quantity, OrderStatus.REJECTED)

        fun buildAcceptedOrder(book: Book, quantity: Int) =
            Order(book.isbn, "${book.title} - ${book.author}", book.price, quantity, OrderStatus.ACCEPTED)

        fun buildDispatchedOrder(existingOrder: Order) = existingOrder.copy(status = OrderStatus.DISPATCHED)
    }
}