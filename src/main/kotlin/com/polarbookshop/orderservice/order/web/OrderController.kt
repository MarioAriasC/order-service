package com.polarbookshop.orderservice.order.web

import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("orders")
class OrderController(private val service: OrderService) {
    @GetMapping
    fun getAllOrders(): Flow<Order> =
        service.getAllOrders()

    @PostMapping
    suspend fun submitOrder(@RequestBody @Valid orderRequest: OrderRequest) =
        service.submitOrder(orderRequest.isbn, orderRequest.quantity!!)
}