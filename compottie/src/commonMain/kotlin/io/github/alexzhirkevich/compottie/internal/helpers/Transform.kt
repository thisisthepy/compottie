package io.github.alexzhirkevich.compottie.internal.helpers

import io.github.alexzhirkevich.compottie.internal.animation.AnimatedTransform
import io.github.alexzhirkevich.compottie.internal.animation.AnimatedVector2
import io.github.alexzhirkevich.compottie.internal.animation.AnimatedNumber
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class Transform(

    @SerialName("a")
    override val anchorPoint : AnimatedVector2? = null,

    @SerialName("p")
    override val position : AnimatedVector2? = null,

    @SerialName("s")
    override val scale : AnimatedVector2? = null,

    @SerialName("r")
    override val rotation : AnimatedNumber? = null,

    @SerialName("o")
    override val opacity : AnimatedNumber? = null,

    @SerialName("sk")
    override val skew: AnimatedNumber? = null,

    @SerialName("sa")
    override val skewAxis: AnimatedNumber? = null,
) : AnimatedTransform()


