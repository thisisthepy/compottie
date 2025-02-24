package io.github.alexzhirkevich.compottie.internal.shapes

import androidx.compose.ui.graphics.Path
import io.github.alexzhirkevich.compottie.dynamic.DynamicEllipseProvider
import io.github.alexzhirkevich.compottie.dynamic.DynamicShapeLayerProvider
import io.github.alexzhirkevich.compottie.dynamic.DynamicShapeProvider
import io.github.alexzhirkevich.compottie.dynamic.derive
import io.github.alexzhirkevich.compottie.dynamic.layerPath
import io.github.alexzhirkevich.compottie.dynamic.toOffset
import io.github.alexzhirkevich.compottie.dynamic.toSize
import io.github.alexzhirkevich.compottie.internal.AnimationState
import io.github.alexzhirkevich.compottie.internal.content.Content
import io.github.alexzhirkevich.compottie.internal.content.PathContent
import io.github.alexzhirkevich.compottie.internal.animation.AnimatedVector2
import io.github.alexzhirkevich.compottie.internal.animation.defaultPosition
import io.github.alexzhirkevich.compottie.internal.animation.defaultScale
import io.github.alexzhirkevich.compottie.internal.animation.dynamicOffset
import io.github.alexzhirkevich.compottie.internal.animation.dynamicSize
import io.github.alexzhirkevich.compottie.internal.helpers.CompoundTrimPath
import io.github.alexzhirkevich.compottie.internal.helpers.CompoundSimultaneousTrimPath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("el")
internal class EllipseShape(

    @SerialName("mn")
    override val matchName : String? = null,

    @SerialName("nm")
    override val name : String? = null,

    @SerialName("hd")
    override val hidden : Boolean = false,

    @SerialName("d")
    val direction : Int = 1,

    @SerialName("p")
    val position : AnimatedVector2 = AnimatedVector2.defaultPosition(),

    @SerialName("s")
    val size : AnimatedVector2,
) : Shape, PathContent {

    @Transient
    private val path = Path()

    @Transient
    private var trimPaths: CompoundTrimPath? = null

    @Transient
    private var dynamicShape : DynamicShapeProvider? = null

    override fun getPath(state: AnimationState): Path {

        if (dynamicShape?.hidden.derive(hidden, state)) {
            path.reset()
            return path
        }

        val size = size.interpolated(state).toSize()

        val halfWidth = size.width / 2f
        val halfHeight = size.height / 2f

        // TODO: handle bounds
        val cpW = halfWidth * ELLIPSE_CONTROL_POINT_PERCENTAGE
        val cpH = halfHeight * ELLIPSE_CONTROL_POINT_PERCENTAGE

        path.reset()
//        if (circleShape.isReversed) {
//            path.moveTo(0f, -halfHeight)
//            path.cubicTo(0 - cpW, -halfHeight, -halfWidth, 0 - cpH, -halfWidth, 0f)
//            path.cubicTo(-halfWidth, 0 + cpH, 0 - cpW, halfHeight, 0f, halfHeight)
//            path.cubicTo(0 + cpW, halfHeight, halfWidth, 0 + cpH, halfWidth, 0f)
//            path.cubicTo(halfWidth, 0 - cpH, 0 + cpW, -halfHeight, 0f, -halfHeight)
//        } else {
        path.moveTo(0f, -halfHeight)
        path.cubicTo(0 + cpW, -halfHeight, halfWidth, 0 - cpH, halfWidth, 0f)
        path.cubicTo(halfWidth, 0 + cpH, 0 + cpW, halfHeight, 0f, halfHeight)
        path.cubicTo(0 - cpW, halfHeight, -halfWidth, 0 + cpH, -halfWidth, 0f)
        path.cubicTo(-halfWidth, 0 - cpH, 0 - cpW, -halfHeight, 0f, -halfHeight)
//        }

        val position = position.interpolated(state).toOffset()

        path.translate(position)

        path.close()

        trimPaths?.apply(path, state)

        return path
    }

    override fun setDynamicProperties(basePath: String?, properties: DynamicShapeLayerProvider?) {
        super.setDynamicProperties(basePath, properties)

        if (name != null) {
            dynamicShape = properties?.get(layerPath(basePath, name))
            val dynamicEllipse = dynamicShape as? DynamicEllipseProvider?
            size.dynamicSize(dynamicEllipse?.size)
            position.dynamicOffset(dynamicEllipse?.position)
        }
    }
    override fun setContents(contentsBefore: List<Content>, contentsAfter: List<Content>) {
        trimPaths = CompoundSimultaneousTrimPath(contentsBefore)
    }

    override fun deepCopy(): Shape {
        return EllipseShape(
            matchName = matchName,
            name = name,
            hidden = hidden,
            direction = direction,
            position = position.copy(),
            size = size.copy()
        )
    }
}
private const val ELLIPSE_CONTROL_POINT_PERCENTAGE = 0.55228f
