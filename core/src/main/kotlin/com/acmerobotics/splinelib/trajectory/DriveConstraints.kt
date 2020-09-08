package com.acmerobotics.splinelib.trajectory

import com.acmerobotics.splinelib.Pose2d
import com.acmerobotics.splinelib.util.MathUtil
import kotlin.math.sqrt

/**
 * This class describes general robot trajectory constraints. More specifically, for paths, the robot wheel velocities,
 * robot acceleration, and centripetal acceleration are limited.  For point turns, the angular velocity and angular
 * acceleration are limited.
 */
open class DriveConstraints @JvmOverloads constructor(
        val maximumVelocity: Double,
        val maximumAcceleration: Double,
        val maximumAngularVelocity: Double,
        val maximumAngularAcceleration: Double,
        val maximumCentripetalAcceleration: Double = Double.NaN,
        private val driveModifier: DriveModifier? = null
) : TrajectoryConstraints {
    override fun maximumVelocity(pose: Pose2d, poseDeriv: Pose2d, poseSecondDeriv: Pose2d): Double {
        val maximumVelocities = mutableListOf(maximumVelocity)

        if (!maximumCentripetalAcceleration.isNaN()) {
            val curvature = MathUtil.curvature(poseDeriv.pos(), poseSecondDeriv.pos())
            maximumVelocities.add(sqrt(maximumCentripetalAcceleration / curvature))
        }

        if (driveModifier != null) {
            val robotPoseDeriv = Pose2d(poseDeriv.pos().rotated(-pose.heading), poseDeriv.heading)
            maximumVelocities.add(driveModifier.getMaximumRobotVelocity(robotPoseDeriv, maximumVelocity))
        }

        return maximumVelocities.min() ?: 0.0
    }

    override fun maximumAcceleration(pose: Pose2d, poseDeriv: Pose2d, poseSecondDeriv: Pose2d) = maximumAcceleration
}
