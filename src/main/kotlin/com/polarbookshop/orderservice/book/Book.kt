package com.polarbookshop.orderservice.book

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 10/2/23
 * Time: 9:43 am
 */
data class Book(
    val isbn: String,
    val title: String,
    val author: String,
    val price: Double
)
