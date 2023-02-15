package com.polarbookshop.orderservice.order.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 10/2/23
 * Time: 8:59 am
 */
@Table("orders")
data class Order(
    @field:Id
    val id: Long?,

    val bookIsbn: String,
    val bookName: String?,
    val bookPrice: Double?,
    val quantity: Int,
    val status: OrderStatus,

    @field:CreatedDate
    val createdDate: Instant?,

    @field:LastModifiedDate
    val lastModifiedDate: Instant?,

    @field:Version
    val version: Int
) {
    companion object {
        operator fun invoke(bookIsbn: String, bookName: String?, bookPrice: Double?, quantity: Int, status: OrderStatus) =
            Order(null, bookIsbn, bookName, bookPrice, quantity, status, null, null, 0)
    }
}
