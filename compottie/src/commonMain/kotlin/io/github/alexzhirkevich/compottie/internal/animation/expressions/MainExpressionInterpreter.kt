package io.github.alexzhirkevich.compottie.internal.animation.expressions

import androidx.compose.ui.util.fastForEach
import io.github.alexzhirkevich.compottie.Compottie

internal class MainExpressionInterpreter(
    expr : String,
    private val context: EvaluationContext
) : ExpressionInterpreter {

    private val expressions = try {
        val lines = expr
            .replace("\t", " ")
            .replace("\r", "")
            .replace("\n{", "{")
            .split(";", "\n")
            .filter(String::isNotBlank)

        buildList {
            var i = 0

            while (i < lines.size) {

                var line = lines[i]

                while (
                    line.endsWithExpressionChar() ||
                    line.countOpenedBlocks()> 0
                ) {

                    check(i < lines.lastIndex) {
                        "Unexpected end of line: $line"
                    }
                    line += if (line.endsWith("{")) {
                        lines[i + 1]
                    } else {
                        ";" + lines[i + 1]
                    }
                    i++
                }

                add(line)
                i++
            }
        }.map {
            if (EXPR_DEBUG_PRINT_ENABLED) {
                println("Expressions: $expr")
            }
            try {
                SingleExpressionInterpreter(it, context).interpret()
            } catch (t: Throwable) {
                Compottie.logger?.warn(
                    "Unsupported or invalid Lottie expression: $it. You can ignore it if the animation runs fine or expressions are disabled (${t.message})"
                )
                throw t
            }
        }
    } catch (t: Throwable) {
        if (EXPR_DEBUG_PRINT_ENABLED) {
            t.printStackTrace()
        }
        emptyList()
    }

    override fun interpret(): Expression {
        return Expression { property, variables, state ->
            expressions.fastForEach {
                it(property, variables, state)
            }
        }
    }
}

private val expressionChars = setOf('=', '+', '-', '*', '/')
private fun String.endsWithExpressionChar() = last { it != ' ' } in expressionChars
private fun String.countOpenedBlocks() : Int {
    var inString : Char? = null
    var openedBlocks = 0
    forEach {
        when {
            inString == null && it == '\'' || it == '"' -> {
                inString = it
            }
            it == inString -> {
                inString = null
            }
            it == '{' -> {
                openedBlocks++
            }
            it == '}' -> {
                openedBlocks--
            }
        }
    }
    return openedBlocks
}
