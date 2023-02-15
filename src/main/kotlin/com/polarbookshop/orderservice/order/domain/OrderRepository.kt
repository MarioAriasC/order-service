package com.polarbookshop.orderservice.order.domain

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderRepository : CoroutineCrudRepository<Order, Long> {
}