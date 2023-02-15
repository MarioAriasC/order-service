package com.polarbookshop.orderservice.book

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient


@TestMethodOrder(MethodOrderer.Random::class)
class BookClientTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var bookClient: BookClient

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toUri().toString())
            .build()
        bookClient = BookClient(webClient)
    }

    @AfterEach
    fun clean() {
        mockWebServer.shutdown()
    }

    @Test
    fun `when book exists then return book`(): Unit = runBlocking {
        val bookIsbn = "1234567890"
        val mockResponse = MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(
                """
                {
                  "isbn": "$bookIsbn",
                  "title": "Title",
                  "author": "Author",
                  "price": 9.90,
                  "publisher": "Polarsophia"
                }
            """.trimIndent()
            )
        mockWebServer.enqueue(mockResponse)
        val book = bookClient.get(bookIsbn)
        assertThat(book).isNotNull
        assertThat(book!!.isbn).isEqualTo(bookIsbn)
    }

    @Test
    fun `when book does not exists then return null`(): Unit = runBlocking {
        val bookIsbn = "1234567891"

        val mockResponse = MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setResponseCode(404)

        mockWebServer.enqueue(mockResponse)

        val book = bookClient.get(bookIsbn)
        assertThat(book).isNull()
    }
}