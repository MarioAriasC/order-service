package com.polarbookshop.orderservice.order.web

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 10/2/23
 * Time: 9:30 am
 */
data class OrderRequest(
    @field:NotBlank(message = "The book ISBN must be defined.")
    val isbn: String,

    @field:NotNull(message = "The book quantity must be defined.")
    @field:Min(value = 1, message = "You must order at least 1 item.")
    @field:Max(value = 5, message = "You cannot order more than 5 items.")
    val quantity:Int?
)
