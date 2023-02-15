package com.polarbookshop.orderservice

import com.polarbookshop.orderservice.book.Book
import com.polarbookshop.orderservice.book.BookClient
import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderStatus
import com.polarbookshop.orderservice.order.web.OrderRequest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderServiceApplicationTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var bookClient: BookClient

    @Test
    fun `when get orders then return`(): Unit = runBlocking {
        val isbn = "1234567893"
        val book = Book(isbn, "Title", "Author", 9.90)
        given(bookClient.get(isbn)).willReturn(book)
        val request = OrderRequest(isbn, 1)
        val expectedOrder = webTestClient
            .post()
            .uri("/orders")
            .bodyValue(request)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(Order::class.java)
            .returnResult()
            .responseBody

        assertThat(expectedOrder).isNotNull

        webTestClient
            .get()
            .uri("/orders")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList(Order::class.java)
            .value<ListBodySpec<Order>> { orders -> assertThat(orders.filter { order -> order.bookIsbn == isbn }).isNotEmpty }
    }

    @Test
    fun `when post request and book exists then the order is accepted`(): Unit = runBlocking {
        val isbn = "1234567899"
        val book = Book(isbn, "Title", "Author", 9.90)
        given(bookClient.get(isbn)).willReturn(book)
        val request = OrderRequest(isbn, 3)

        val createdOrder = webTestClient
            .post()
            .uri("/orders")
            .bodyValue(request)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(Order::class.java)
            .returnResult()
            .responseBody

        assertThat(createdOrder).isNotNull
        assertThat(createdOrder!!.bookIsbn).isEqualTo(request.isbn)
        assertThat(createdOrder.quantity).isEqualTo(request.quantity)
        assertThat(createdOrder.bookName).isEqualTo("${book.title} - ${book.author}")
        assertThat(createdOrder.bookPrice).isEqualTo(book.price)
        assertThat(createdOrder.status).isEqualTo(OrderStatus.ACCEPTED)
    }

    @Test
    fun `when post request and book does not exists then order is rejected`(): Unit = runBlocking {
        val isbn = "1234567894"
        given(bookClient.get(isbn)).willReturn(null)
        val request = OrderRequest(isbn, 3)

        val createdOrder = webTestClient
            .post()
            .uri("/orders")
            .bodyValue(request)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(Order::class.java)
            .returnResult()
            .responseBody

        assertThat(createdOrder).isNotNull
        assertThat(createdOrder!!.bookIsbn).isEqualTo(request.isbn)
        assertThat(createdOrder.quantity).isEqualTo(request.quantity)
        assertThat(createdOrder.status).isEqualTo(OrderStatus.REJECTED)
    }

    companion object {
        @JvmStatic
        @Container
        val postgresql = PostgreSQLContainer(DockerImageName.parse("postgres:14.4"))

        @JvmStatic
        @DynamicPropertySource
        fun postgresqlProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") {
                "r2dbc:postgresql://${postgresql.host}:${postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)}/${postgresql.databaseName}"
            }
            registry.add("spring.r2dbc.username", postgresql::getUsername)
            registry.add("spring.r2dbc.password", postgresql::getPassword)
            registry.add("spring.flyway.url", postgresql::getJdbcUrl)

        }
    }
}
