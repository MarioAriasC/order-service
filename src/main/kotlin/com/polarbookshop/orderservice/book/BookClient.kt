package com.polarbookshop.orderservice.book

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@Component
class BookClient(private val webClient: WebClient) {

    @Deprecated(message = "Use suspend get")
    fun getBookByIsbn(isbn: String): Mono<Book> = webClient
        .get()
        .uri(BOOK_ROOT_API + isbn)
        .retrieve()
        .bodyToMono(Book::class.java)

    suspend fun get(isbn: String): Book? = webClient
        .get()
        .uri(BOOK_ROOT_API + isbn)
        .retrieve()
        .bodyToMono<Book>()
        .timeout(Duration.ofSeconds(3), Mono.empty())
        .onErrorResume(WebClientResponseException.NotFound::class.java, handler)
        .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
        .onErrorResume(Exception::class.java, handler)
        .awaitSingleOrNull()

    companion object {
        const val BOOK_ROOT_API = "/books/"
        val handler: (t: Exception) -> Mono<out Book> = { _ -> Mono.empty() }
    }
}