package com.acmerobotics.splinelib.trajectory

import com.acmerobotics.splinelib.Pose2d
import kotlin.math.max
import kotlin.math.min

class Trajectory(segments: List<TrajectorySegment> = listOf()) {
    val segments: MutableList<TrajectorySegment> = segments.toMutableList()

    fun duration() = segments.map { it.duration() }.sum()

    operator fun get(time: Double): Pose2d {
        var remainingTime = max(0.0, min(time, duration()))
        for (segment in segments) {
            if (remainingTime <= segment.duration()) {
                return segment[remainingTime]
            }
            remainingTime -= segment.duration()
        }
        return segments.lastOrNull()?.get(segments.last().duration()) ?: Pose2d()
    }

    fun velocity(time: Double): Pose2d {
        var remainingTime = max(0.0, min(time, duration()))
        for (segment in segments) {
            if (remainingTime <= segment.duration()) {
                return segment.velocity(remainingTime)
            }
            remainingTime -= segment.duration()
        }
        return segments.lastOrNull()?.velocity(segments.last().duration()) ?: Pose2d()
    }

    fun acceleration(time: Double): Pose2d {
        var remainingTime = max(0.0, min(time, duration()))
        for (segment in segments) {
            if (remainingTime <= segment.duration()) {
                return segment.acceleration(remainingTime)
            }
            remainingTime -= segment.duration()
        }
        return segments.lastOrNull()?.acceleration(segments.last().duration()) ?: Pose2d()
    }

    fun start() = get(0.0)

    fun startVelocity() = velocity(0.0)

    fun startAcceleration() = acceleration(0.0)

    fun end() = get(duration())

    fun endVelocity() = velocity(duration())

    fun endAcceleration() = acceleration(duration())
}