package io.github.alexzhirkevich.compottie.internal.platform

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathOperation

expect fun ExtendedPathMeasure() : ExtendedPathMeasure

interface ExtendedPathMeasure : PathMeasure {
    fun nextContour() : Boolean
}

fun Path.set(other : Path){
    op(this, other, PathOperation.Union)
}