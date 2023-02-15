package com.polarbookshop.orderservice.order.web

import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderService
import com.polarbookshop.orderservice.order.domain.OrderStatus
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(OrderController::class)
class OrderControllerWebFluxTests {
    @Autowired
    lateinit var webClient: WebTestClient

    @MockBean
    lateinit var service: OrderService

    @Test
    fun `when book is not available then reject order`(): Unit = runBlocking {
        val request = OrderRequest("1234567890", 3)
        val order = OrderService.buildRejectedOrder(request.isbn, request.quantity!!)
        given(service.submitOrder(request.isbn, request.quantity!!)).willReturn(order)

        webClient.post()
            .uri("/orders")
            .bodyValue(request)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(Order::class.java)
            .value { actualOrder ->
                assertThat(actualOrder).isNotNull
                assertThat(actualOrder.status).isEqualTo(OrderStatus.REJECTED)
            }
    }
}