package io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.composition

import io.github.alexzhirkevich.compottie.internal.animation.expressions.Expression
import io.github.alexzhirkevich.compottie.internal.animation.expressions.ExpressionContext
import io.github.alexzhirkevich.compottie.internal.animation.expressions.Undefined
import io.github.alexzhirkevich.compottie.internal.animation.expressions.argAt
import io.github.alexzhirkevich.compottie.internal.animation.expressions.argForNameOrIndex
import io.github.alexzhirkevich.compottie.internal.animation.expressions.checkArgs
import io.github.alexzhirkevich.compottie.internal.animation.expressions.checkArgsNotNull
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.value.OpConstant
import io.github.alexzhirkevich.compottie.internal.layers.Layer
import io.github.alexzhirkevich.compottie.internal.layers.PrecompositionLayer

internal sealed class OpLayerContext : Expression, ExpressionContext<Layer> {

    override fun interpret(callable: String?, args: List<Expression>?): Expression? {
        return when (callable) {
            "index" -> withContext { _, _, _ -> index ?: Undefined }
            "name" -> withContext { _, _, _ -> name ?: Undefined }
            "inPoint" -> withContext { _, _, s ->
                inPoint?.div(s.composition.frameRate) ?: Undefined
            }

            "outPoint" -> withContext { _, _, s ->
                outPoint?.div(s.composition.frameRate) ?: Undefined
            }

            "startTime" -> withContext { _, _, s ->
                startTime?.div(s.composition.frameRate) ?: Undefined
            }

            "source" -> withContext { _, _, _ ->
                if (this is PrecompositionLayer) composition else Undefined
            }

            "content" -> {
                checkArgs(args, 1, callable)
                OpGetShape(
                    layerOrGroup = this,
                    name = args.argAt(0)
                )
            }
            "active" -> withContext { _, _, s -> isActive(s) }
            "enabled" -> withContext { _, _, s -> !isHidden(s) }
            "hasParent" -> withContext { _, _, _ -> parentLayer != null }
            "parent" -> withContext { _, _, _ -> parentLayer ?: Undefined }
            "transform" -> OpGetLayerTransform(this)
            "effect" -> {
                checkArgs(args, 1, callable)
                return OpGetEffect(layer = this, nameOrIndex = args.argAt(0))
            }

            "rotation" -> withContext { _, _, _ ->
                OpGetProperty { _, _, _ -> transform.rotation }
            }

            "position" -> withContext { _, _, _ ->
                OpGetProperty { _, _, _ -> transform.position }
            }

            "scale" -> withContext { _, _, _ ->
                OpGetProperty { _, _, _ -> transform.scale }
            }

            "opacity" -> withContext { _, _, _ ->
                OpGetProperty { _, _, _ -> transform.opacity }
            }

            "timeRemap" -> withContext { _, _, _ ->
                OpGetProperty { _, _, _ ->
                    (this as? PrecompositionLayer)?.timeRemapping ?: Undefined
                }
            }

            "toComp" -> {
                checkArgs(args, 1, callable)
                OpLayerToComp(
                    layer = this,
                    point = args.argForNameOrIndex(0,"point")!!,
                    time = args.argForNameOrIndex(1,"t"),
                    reverse = false
                )
            }

            "fromComp" -> {
                checkArgsNotNull(args, callable)
                OpLayerToComp(
                    layer = this,
                    point = args.argForNameOrIndex(0,"point")!!,
                    time = args.argForNameOrIndex(1,"t"),
                    reverse = true
                )
            }

            "toWorld" -> {
                checkArgsNotNull(args, callable)
                OpLayerToWorld(
                    layer = this,
                    point = args.argForNameOrIndex(0,"point")!!,
                    time = args.argForNameOrIndex(1,"t"),
                    reverse = false
                )
            }

            "fromWorld" -> {
                checkArgs(args, 1, callable)
                OpLayerToWorld(
                    layer = this,
                    point = args.argForNameOrIndex(0, "point")!!,
                    time = args.argForNameOrIndex(1, "t"),
                    reverse = true
                )
            }

            "hasAudio", "hasVideo", "audioActive" -> OpConstant(false)
            "sourceRectAtTime", "sampleImage" -> error("$callable for Layer is not yet supported")
            else -> null
        }
    }
}