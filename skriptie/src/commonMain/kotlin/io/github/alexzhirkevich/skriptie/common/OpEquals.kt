package io.github.alexzhirkevich.skriptie.common

import io.github.alexzhirkevich.skriptie.Expression
import io.github.alexzhirkevich.skriptie.invoke

internal fun  OpEquals(
    a : Expression,
    b : Expression,
    isTyped : Boolean
) = Expression {
    OpEqualsImpl(a(it), b(it), isTyped)
}

internal fun OpEqualsImpl(a : Any?, b : Any?, typed : Boolean) : Boolean {

    return when {
        a == null || b == null -> a == b
        a is Number && b is Number -> a.toDouble() == b.toDouble()
        typed || a::class == b::class -> a == b
        a is String && b is Number -> a.toDoubleOrNull() == b.toDouble()
        b is String && a is Number -> b.toDoubleOrNull() == a.toDouble()
        else -> a.toString() == b.toString()
    }
}