package io.github.alexzhirkevich.compottie.internal.assets

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class CharacterData(

    @SerialName("ch")
    val character : String,

    @SerialName("fFamily")
    val fontFamily : String,

    @SerialName("size")
    val fontSize : Float = 10f,

    @SerialName("style")
    val fontStyle : String? = null,

    @SerialName("width")
    val width : Float? = null,

    val data: CharacterData
)