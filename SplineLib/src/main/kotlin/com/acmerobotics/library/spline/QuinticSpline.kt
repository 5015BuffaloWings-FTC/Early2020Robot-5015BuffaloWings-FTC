package com.acmerobotics.library.spline

import com.acmerobotics.library.Pose2d
import com.acmerobotics.library.Waypoint

class QuinticSpline(val segments: List<QuinticSplineSegment>) {
    companion object {
        fun fromWaypoints(vararg waypoints: Waypoint) =
            QuinticSpline((0 until waypoints.lastIndex).map { QuinticSplineSegment(waypoints[it], waypoints[it+1]) })

        fun fromPoses(vararg poses: Pose2d, interpolator: HeadingInterpolator = TangentInterpolator()): QuinticSpline {
            return fromWaypoints(*(0 until poses.size).map { Waypoint(poses[it].x, poses[it].y,
                Math.cos(poses[it].heading), Math.sin(poses[it].heading)) }.toTypedArray())
        }
    }

    fun length() = segments.sumByDouble { it.length() }

    operator fun get(displacement: Double): Pose2d {
        var remainingDisplacement = displacement
        for (segment in segments) {
            if (remainingDisplacement <= segment.length()) {
                return segment.pose(remainingDisplacement)
            }
            remainingDisplacement -= segment.length()
        }
        return end()
    }

    fun deriv(displacement: Double): Pose2d {
        var remainingDisplacement = displacement
        for (segment in segments) {
            if (remainingDisplacement <= segment.length()) {
                return segment.poseDeriv(remainingDisplacement)
            }
            remainingDisplacement -= segment.length()
        }
        return endDeriv()
    }

    fun secondDeriv(displacement: Double): Pose2d {
        var remainingDisplacement = displacement
        for (segment in segments) {
            if (remainingDisplacement <= segment.length()) {
                return segment.poseSecondDeriv(remainingDisplacement)
            }
            remainingDisplacement -= segment.length()
        }
        return endSecondDeriv()
    }

    fun end() = segments.last().endPose()

    fun endDeriv() = segments.last().endPoseDeriv()

    fun endSecondDeriv() = segments.last().endPoseSecondDeriv()
}
