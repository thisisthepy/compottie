package io.github.alexzhirkevich.compottie

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.toByteArray

internal val DefaultHttpClient by lazy {
    HttpClient {
        followRedirects = true
        expectSuccess = true
        install(HttpRequestRetry) {
            maxRetries = 2
            constantDelay(1000, 500)
        }
    }
}

internal val DefaultHttpRequest : suspend  (String) -> ByteArray = {
    DefaultHttpClient.get(it).bodyAsChannel().toByteArray()
}