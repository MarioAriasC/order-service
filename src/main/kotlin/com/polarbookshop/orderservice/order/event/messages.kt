package com.polarbookshop.orderservice.order.event

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 18/2/23
 * Time: 11:20 am
 */
data class OrderDispatchedMessage(val orderId: Long)
data class OrderAcceptedMessage(val orderId: Long)
