package com.polarbookshop.orderservice.order.web

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class OrderRequestValidationTests {

    @Test
    fun `when all fields are correct then validation succeeds`() {
        val request = OrderRequest("1234567890", 1)
        val violations = validator.validate(request)
        assertThat(violations).isEmpty()
    }

    @Test
    fun `when isbn is not defined then validations fails`() {
        val request = OrderRequest("", 1)
        val violations = validator.validate(request)
        assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().message).isEqualTo("The book ISBN must be defined.")
    }

    @Test
    fun `when quantity is not defined then validations fails`() {
        val request = OrderRequest("1234567890", null)
        val violations = validator.validate(request)
        assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().message).isEqualTo("The book quantity must be defined.")
    }

    @Test
    fun `when quantity is lower than min then validation fails`() {
        val request = OrderRequest("1234567890", 0)
        val violations = validator.validate(request)
        assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().message).isEqualTo("You must order at least 1 item.")
    }

    @Test
    fun `when quantity is greater than max then validation fails`() {
        val request = OrderRequest("1234567890", 7)
        val violations = validator.validate(request)
        assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().message).isEqualTo("You cannot order more than 5 items.")
    }

    companion object {
        lateinit var validator: Validator

        @JvmStatic
        @BeforeAll
        fun setup() {
            val factory = Validation.buildDefaultValidatorFactory()!!
            validator = factory.validator
        }
    }
}