package com.acmerobotics.roadrunner.drive

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.localization.Localizer
import com.acmerobotics.roadrunner.util.Angle

/**
 * Abstraction for generic robot drive motion and localization. Robot poses are specified in a coordinate system with
 * positive x pointing forward, positive y pointing left, and positive heading measured counter-clockwise from the
 * x-axis.
 */
abstract class Drive {
    /**
     * Localizer used to determine the evolution of [poseEstimate].
     */
    abstract var localizer: Localizer

    private var headingOffset: Double = 0.0

    /**
     * The raw heading used for computing [externalHeading]. Not affected by [externalHeading] setter.
     */
    protected abstract val rawExternalHeading: Double

    /**
     * The robot's heading in radians as measured by an external sensor (e.g., IMU, gyroscope).
     */
    var externalHeading: Double
        get() = Angle.norm(rawExternalHeading + headingOffset)
        set(value) {
            headingOffset = -rawExternalHeading + value
        }

    /**
     * The robot's current pose estimate.
     */
    var poseEstimate: Pose2d
        get() = localizer.poseEstimate
        set(value) {
            localizer.poseEstimate = value
        }

    /**
     * Updates [poseEstimate] with the most recent positional change.
     */
    fun updatePoseEstimate() {
        localizer.update()
    }

    /**
     * Sets the [driveSignal] of the robot.
     */
    abstract fun setDriveSignal(driveSignal: DriveSignal)
}
