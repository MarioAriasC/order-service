package com.polarbookshop.orderservice.order.web

import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import java.time.Instant

@JsonTest
class OrderJsonTests {
    @Autowired
    lateinit var json: JacksonTester<Order>

    @Test
    fun serialise() {
        val order =
            Order(349, "1234567890", "Book name", 9.90, 1, OrderStatus.ACCEPTED, Instant.now(), Instant.now(), 21)

        val jsonContent = json.write(order)

        with(assertThat(jsonContent)) {
            extractingJsonPathNumberValue("@.id")
                .isEqualTo(order.id?.toInt())
            extractingJsonPathStringValue("@.bookIsbn")
                .isEqualTo(order.bookIsbn)
            extractingJsonPathStringValue("@.bookName")
                .isEqualTo(order.bookName)
            extractingJsonPathNumberValue("@.bookPrice")
                .isEqualTo(order.bookPrice)
            extractingJsonPathNumberValue("@.quantity")
                .isEqualTo(order.quantity)
            extractingJsonPathStringValue("@.status")
                .isEqualTo(order.status.toString())
            extractingJsonPathStringValue("@.createdDate")
                .isEqualTo(order.createdDate.toString())
            extractingJsonPathStringValue("@.lastModifiedDate")
                .isEqualTo(order.lastModifiedDate.toString())
            extractingJsonPathNumberValue("@.version")
                .isEqualTo(order.version)
        }


    }
}