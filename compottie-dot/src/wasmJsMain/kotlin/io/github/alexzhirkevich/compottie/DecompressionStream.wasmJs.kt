package io.github.alexzhirkevich.compottie

import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Int8Array
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

internal external class DecompressionStream(alg : String)

internal external class ReadableStream(
    source: JsAny
) {
    fun pipeThrough(decompressionStream: DecompressionStream) : ReadableStream

    fun getReader() : StreamReader
}

internal external interface StreamReader {
    fun read() : Promise<StreamReadResult>
}

internal external interface StreamReadResult : JsAny {
    val done : Boolean

    val value : ArrayBufferView
}

internal suspend fun <T : JsAny?> Promise<T>.await() = suspendCoroutine { cont ->
    then {
        cont.resume(it)
        null
    }.catch {
        cont.resumeWithException(Exception(it.toString()))
        null
    }
}