package com.acmerobotics.library

class SimpleMotionConstraints(val maximumVelocity: Double, val maximumAcceleration: Double): MotionConstraints {
    override fun maximumVelocity(displacement: Double) = maximumVelocity
    override fun maximumAcceleration(displacement: Double) = maximumAcceleration
}