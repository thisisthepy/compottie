package io.github.alexzhirkevich.skriptie.common

import io.github.alexzhirkevich.skriptie.Expression
import io.github.alexzhirkevich.skriptie.ScriptRuntime
import io.github.alexzhirkevich.skriptie.VariableType
import io.github.alexzhirkevich.skriptie.argForNameOrIndex
import io.github.alexzhirkevich.skriptie.ecmascript.ESAny
import io.github.alexzhirkevich.skriptie.ecmascript.ESObject
import io.github.alexzhirkevich.skriptie.invoke

public class FunctionParam(
    public val name : String,
    public val isVararg : Boolean = false,
    public val default : Expression? = null
)

internal infix fun String.defaults(default: Expression?) : FunctionParam {
    return FunctionParam(this, false, default)
}


internal interface Callable {
    operator fun invoke(args: List<Expression>, context: ScriptRuntime) : Any?
}

internal class Function(
    val name : String,
    val parameters : List<FunctionParam>,
    private val body : Expression
) : Callable {
    init {
        val varargs = parameters.count { it.isVararg }

        if (varargs > 1 || varargs == 1 && !parameters.last().isVararg){
            throw SyntaxError("Rest parameter must be last formal parameter")
        }
    }

    override fun invoke(
        args: List<Expression>,
        context: ScriptRuntime,
    ): Any? {
        try {
            val arguments = buildMap {
                parameters.fastForEachIndexed { i, p ->
                    val value = if (p.isVararg){
                        args.drop(i).fastMap { it(context) }
                    } else {
                        requireNotNull(args.argForNameOrIndex(i, p.name) ?: p.default) {
                            "'${p.name}' argument of '$name' function is missing"
                        }.invoke(context)
                    }
                    this[p.name] = Pair(
                        VariableType.Local,
                        value
                    )
                }
            }
            return context.withScope(arguments, body::invoke)
        } catch (ret: BlockReturn) {
            return ret.value
        }
    }
}

internal fun OpFunctionExec(
    name : String,
    receiver : Expression?,
    parameters : List<Expression>,
) = Expression { ctx ->

    val res = receiver?.invoke(ctx)
    val function = when {
        res == null -> ctx[name]
        res is Callable && name.isBlank() -> res
        res is ESObject -> res[name]
        res is ESAny -> {
            return@Expression res.invoke(name, ctx, parameters)
        }
        else -> null
    }
    if (function is Unit){
        unresolvedReference(name)
    }
    if (function !is Callable){
        throw TypeError("$name ($function) is not a function")
    }
    function.invoke(
        args = parameters,
        context = ctx,
    )
}
