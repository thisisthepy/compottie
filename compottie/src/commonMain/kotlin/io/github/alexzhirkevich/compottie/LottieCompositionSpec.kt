package io.github.alexzhirkevich.compottie

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
sealed interface LottieCompositionSpec {
    suspend fun load() : LottieComposition

    companion object
}


@Stable
fun LottieCompositionSpec.Companion.JsonString(
    jsonString: String
): LottieCompositionSpec = JsonStringImpl(jsonString)


@Immutable
private class JsonStringImpl(
    private val jsonString: String
) : LottieCompositionSpec  {

    override suspend fun load(): LottieComposition {
        return LottieComposition.parse(jsonString)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as JsonStringImpl

        if (jsonString != other.jsonString) return false

        return true
    }

    override fun hashCode(): Int {
        return jsonString.hashCode()
    }

    override fun toString(): String {
        return "JsonString(jsonString='$jsonString')"
    }
}

