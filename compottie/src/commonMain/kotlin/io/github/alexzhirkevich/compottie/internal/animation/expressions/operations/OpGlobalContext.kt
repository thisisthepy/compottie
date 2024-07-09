package io.github.alexzhirkevich.compottie.internal.animation.expressions.operations

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import io.github.alexzhirkevich.compottie.internal.AnimationState
import io.github.alexzhirkevich.compottie.internal.animation.RawProperty
import io.github.alexzhirkevich.compottie.internal.animation.expressions.EXPR_DEBUG_PRINT_ENABLED
import io.github.alexzhirkevich.compottie.internal.animation.expressions.EvaluationContext
import io.github.alexzhirkevich.compottie.internal.animation.expressions.Expression
import io.github.alexzhirkevich.compottie.internal.animation.expressions.ExpressionContext
import io.github.alexzhirkevich.compottie.internal.animation.expressions.Undefined
import io.github.alexzhirkevich.compottie.internal.animation.expressions.checkArgs
import io.github.alexzhirkevich.compottie.internal.animation.expressions.getForNameOrIndex
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.color.OpHslToRgb
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.color.OpRgbToHsl
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.composition.OpGetComp
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.composition.OpGetLayer
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.composition.OpGetProperty
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.composition.OpLayerToComp
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.composition.OpPropertyContext
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.condition.OpIfCondition
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.math.OpAdd
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.math.OpClamp
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.math.OpDegreesToRadians
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.math.OpDiv
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.vec.OpDot
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.math.OpMath
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.math.OpMod
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.math.OpMul
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.math.OpRadiansToDegree
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.math.OpSub
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.random.OpNoise
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.random.OpRandomNumber
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.random.OpSetRandomSeed
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.time.OpFramesToTime
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.time.OpGetTime
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.time.OpInterpolate
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.time.OpTimeToFrames
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.value.OpConstant
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.value.OpGetVariable
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.value.OpVar
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.vec.OpLength
import io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.vec.OpNormalize

internal object OpGlobalContext : ExpressionContext<Undefined>, Expression {

    // global context extends thisProperty context
    private val thisProperty = object : OpPropertyContext(), Expression by OpGetProperty() {}

    override fun interpret(op: String, args: List<Expression>): Expression? {

        return when (op) {
            "var", "let", "const" -> OpVar
            "Math" -> OpMath
            "time" -> OpGetTime
            "thisComp" -> {
                if (args.isEmpty()) {
                    OpGetComp(null)
                } else {
                    OpGetLayer(nameOrIndex = args.single())
                }
            }

            "comp" -> {
                checkArgs(args, 1, op)
                return OpGetComp(args[0])
            }

            "thisLayer" -> OpGetLayer()
            "thisProperty" -> thisProperty
            "add", "\$bm_sum", "sum" -> {
                checkArgs(args, 2, op)
                OpAdd(
                    args.getForNameOrIndex(0, "vec1")!!,
                    args.getForNameOrIndex(1, "vec2")!!,
                )
            }

            "sub", "\$bm_sub" -> {
                checkArgs(args, 2, op)
                OpSub(
                    args.getForNameOrIndex(0, "vec1")!!,
                    args.getForNameOrIndex(1, "vec2")!!,
                )
            }

            "mul", "\$bm_mul" -> {
                checkArgs(args, 2, op)
                OpMul(
                    args.getForNameOrIndex(0, "vec")!!,
                    args.getForNameOrIndex(1, "amount")!!,
                )
            }

            "div", "\$bm_div" -> {
                checkArgs(args, 2, op)
                OpDiv(
                    args.getForNameOrIndex(0, "vec")!!,
                    args.getForNameOrIndex(1, "amount")!!,
                )
            }

            "mod" -> {
                checkArgs(args, 2, op)
                OpMod(
                    args.getForNameOrIndex(0, "vec")!!,
                    args.getForNameOrIndex(1, "amount")!!,
                )
            }

            "clamp" -> {
                checkArgs(args, 3, op)
                OpClamp(
                    args.getForNameOrIndex(0, "value")!!,
                    args.getForNameOrIndex(1, "limit1")!!,
                    args.getForNameOrIndex(2, "limit2")!!,
                )
            }

            "dot" -> {
                checkArgs(args, 2, op)
                OpDot(
                    args.getForNameOrIndex(0, "vec1")!!,
                    args.getForNameOrIndex(1, "vec2")!!,
                )
            }

            "length" -> OpLength(
                args.getForNameOrIndex(0, "vec", "point1")!!,
                args.getForNameOrIndex(1, "point2"),
            )
            "normalize" -> {
                checkArgs(args, 1, op)
                OpNormalize(args[0])
            }

            "cross" -> error("cross is not supported yet") //todo: support OpCross

            "degreesToRadians" -> {
                checkArgs(args, 1, op)
                OpDegreesToRadians(args[0])
            }

            "radiansToDegrees" -> {
                checkArgs(args, 1, op)
                OpRadiansToDegree(args[0])
            }

            "timeToFrames" -> OpTimeToFrames(
                args.getForNameOrIndex(0,"t"),
                args.getForNameOrIndex(1,"fps"),
            )
            "framesToTime" -> OpFramesToTime(
                args.getForNameOrIndex(0,"frames"),
                args.getForNameOrIndex(1,"fps"),
            )
            "seedRandom" -> OpSetRandomSeed(
                args.getForNameOrIndex(0,"offset")!!,
                args.getForNameOrIndex(1,"timeless"),
            )
            "random", "gaussRandom" -> {
                OpRandomNumber(
                    args.getForNameOrIndex(0,"maxValOrArray1"),
                    args.getForNameOrIndex(1,"maxValOrArray2"),
                    isGauss = op == "gaussRandom"
                )
            }

            "noise" -> {
                checkArgs(args, 1, op)
                OpNoise(args[0])
            }

            "linear" -> OpInterpolate.interpret(LinearEasing, args)
            "ease" -> OpInterpolate.interpret(easeInOut, args)
            "easeIn" -> OpInterpolate.interpret(easeIn, args)
            "easeOut" -> OpInterpolate.interpret(easeOut, args)

            "hslToRgb" -> {
                checkArgs(args, 1, op)
                OpHslToRgb(args[0])
            }

            "rgbToHsl" -> {
                checkArgs(args, 1, op)
                OpRgbToHsl(args[0])
            }

            "if" -> {
                OpIfCondition(condition = args.single())
            }//error("Compottie doesn't support conditions in expressions yet")
            "true" -> OpConstant(true)
            "false" -> OpConstant(false)
            else -> {
                thisProperty.interpret(op, args) ?: run {
                    if (args.isEmpty()) {
                        if (EXPR_DEBUG_PRINT_ENABLED) {
                            println("making GetVariable $op...")
                        }
                        OpGetVariable(op)
                    } else {
                        null
                    }
                }
            }
        }
    }

    override fun invoke(
        property: RawProperty<Any>,
        context: EvaluationContext,
        state: AnimationState
    ): Any = Undefined
}

internal val easeInOut = CubicBezierEasing(0.33f, 0f, 0.667f, 1f)
internal val easeOut = CubicBezierEasing(0.167f, 0.167f, 0.667f, 1f)
internal val easeIn = CubicBezierEasing(0.333f, 0f, 0.833f, 0.833f)