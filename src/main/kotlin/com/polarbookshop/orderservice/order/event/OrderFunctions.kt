package com.polarbookshop.orderservice.order.event

import com.polarbookshop.orderservice.order.domain.OrderService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OrderFunctions {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @Bean
    fun dispatchOrder(orderService: OrderService): suspend (Flow<OrderDispatchedMessage>) -> Unit = { flow ->
        orderService.consumeOrderDispatchEvent(flow)
            .onEach { order -> logger.info { "The order with ${order.id} is dispatched" } }
            .collect()
    }
}