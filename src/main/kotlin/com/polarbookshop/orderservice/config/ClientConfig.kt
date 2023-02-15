package com.polarbookshop.orderservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ClientConfig {
    @Bean
    fun webClient(clientProperties: ClientProperties, webClientBuilder: WebClient.Builder): WebClient =
        webClientBuilder
            .baseUrl(clientProperties.catalogServiceUri.toString())
            .build()
}