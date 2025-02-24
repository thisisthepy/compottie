package io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.composition

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import io.github.alexzhirkevich.compottie.internal.AnimationState
import io.github.alexzhirkevich.compottie.internal.animation.RawProperty
import io.github.alexzhirkevich.compottie.internal.animation.expressions.EvaluationContext
import io.github.alexzhirkevich.compottie.internal.animation.expressions.Expression
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.get
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.time.OpGetTime
import io.github.alexzhirkevich.compottie.internal.layers.Layer
import io.github.alexzhirkevich.compottie.internal.layers.totalTransformMatrix

internal class OpLayerToWorld(
    private val layer : Expression,
    private val point : Expression,
    private val time : Expression?,
    private val reverse : Boolean
) : Expression {

    private val conversionMatrix = Matrix()

    override fun invoke(
        property: RawProperty<Any>,
        context: EvaluationContext,
        state: AnimationState
    ): List<Number> {
        val t = time.takeIf { it !is OpGetTime }

        return if (t == null) {
            convert(
                layer = layer(property, context, state) as Layer,
                point = point(property, context, state),
                state = state
            )
        } else {
            state.onTime((t(property, context, state) as Number).toFloat()) {
                convert(
                    layer = layer(property, context, state) as Layer,
                    point = point(property, context, state),
                    state = it
                )
            }
        }
    }

    private fun convert(
        layer: Layer,
        point : Any,
        state: AnimationState,
    ) : List<Number> {
        val layerMatrix = layer.totalTransformMatrix(state)

        val offset = Offset(
            (point[0] as Number).toFloat(),
            (point[1] as Number).toFloat()
        )

        if (!reverse) {
            layerMatrix.invert()
        }

        val result = conversionMatrix.map(offset)

        return listOf(result.x, result.y)
    }
}