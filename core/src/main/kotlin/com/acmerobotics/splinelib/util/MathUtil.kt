package com.acmerobotics.splinelib.util

import com.acmerobotics.splinelib.Vector2d
import kotlin.math.abs

object MathUtil {
    fun curvature(deriv: Vector2d, secondDeriv: Vector2d): Double {
        val norm = deriv.norm()
        return abs(deriv.x * secondDeriv.y - deriv.y * secondDeriv.x) / (norm * norm * norm)
    }
}