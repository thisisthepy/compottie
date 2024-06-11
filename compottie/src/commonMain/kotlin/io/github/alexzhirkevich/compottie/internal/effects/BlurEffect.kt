package io.github.alexzhirkevich.compottie.internal.effects

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("29")
internal class BlurEffect(
    @SerialName("ef")
    override val values : List<EffectValue<@Contextual Any?>>
) : LayerEffect {

    val radius get() = (values.getOrNull(0) as? EffectValue.Slider)?.value
}