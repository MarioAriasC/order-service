package com.polarbookshop.orderservice.order.domain

import com.polarbookshop.orderservice.config.DataConfig
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@DataR2dbcTest
@Import(DataConfig::class)
@Testcontainers
class OrderRepositoryR2dbcTests {
    @Autowired
    lateinit var repository: OrderRepository

    @Test
    fun `find order by id when no order existing`(): Unit = runBlocking {
        assertThat(repository.findById(349)).isNull()
    }

    @Test
    fun `create a rejected order`(): Unit = runBlocking {
        val rejectedOrder = OrderService.buildRejectedOrder("1234567890", 3)
        val order = repository.save(rejectedOrder)
        assertThat(order.status).isEqualTo(OrderStatus.REJECTED)
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