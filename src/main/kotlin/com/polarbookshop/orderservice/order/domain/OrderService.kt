package com.polarbookshop.orderservice.order.domain

import com.polarbookshop.orderservice.book.Book
import com.polarbookshop.orderservice.book.BookClient
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class OrderService(private val repository: OrderRepository, private val client: BookClient) {
    fun getAllOrders(): Flow<Order> = repository.findAll()

    suspend fun submitOrder(isbn: String, quantity: Int): Order {
        val book = client.get(isbn)
        val order = if (book != null) {
            buildAcceptedOrder(book, quantity)
        } else {
            buildRejectedOrder(isbn, quantity)
        }
        return repository.save(order)
    }

    companion object {
        fun buildRejectedOrder(bookIsbn: String, quantity: Int) =
            Order(bookIsbn, null, null, quantity, OrderStatus.REJECTED)

        fun buildAcceptedOrder(book: Book, quantity: Int) =
            Order(book.isbn, "${book.title} - ${book.author}", book.price, quantity, OrderStatus.ACCEPTED)
    }
}