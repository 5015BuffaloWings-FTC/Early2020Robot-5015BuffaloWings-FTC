package com.acmerobotics.splinelib.followers

import com.acmerobotics.splinelib.Pose2d
import com.acmerobotics.splinelib.control.PIDCoefficients
import com.acmerobotics.splinelib.control.PIDFController
import com.acmerobotics.splinelib.drive.TankDrive
import com.acmerobotics.splinelib.drive.TankKinematics
import kotlin.math.sign

/**
 * Traditional PID controller with feedforward velocity and acceleration components to follow a trajectory. More
 * specifically, one feedback loop controls the path displacement (i.e., x in the robot reference frame), and
 * another feedback loop to minimize cross track (lateral) error via heading correction (overall, very similar to
 * [MecanumPIDVAFollower] except adjusted for the nonholonomic constraint). Feedforward is applied at the wheel level.
 *
 * @param drive tank drive instance
 * @param displacementCoeffs PID coefficients for the robot axial (x) controller
 * @param crossTrackCoeffs PID coefficients for the robot heading controller based on cross track error
 * @param kV feedforward velocity gain
 * @param kA feedforward acceleration gain
 * @param kStatic signed, additive feedforward constant (used to overcome static friction)
 */
class TankPIDVAFollower(
        private val drive: TankDrive,
        displacementCoeffs: PIDCoefficients,
        crossTrackCoeffs: PIDCoefficients,
        private val kV: Double,
        private val kA: Double,
        private val kStatic: Double
) : TrajectoryFollower() {
    private val displacementController = PIDFController(displacementCoeffs)
    private val crossTrackController = PIDFController(crossTrackCoeffs)

    override fun internalUpdate(currentPose: Pose2d, currentTimestamp: Double) {
        val t = elapsedTime(currentTimestamp)

        val targetPose = trajectory[t]
        val targetPoseVelocity = trajectory.velocity(t)
        val targetPoseAcceleration = trajectory.acceleration(t)

        val targetRobotPose = Pose2d(targetPose.pos().rotated(-targetPose.heading), targetPose.heading)
        val targetRobotPoseVelocity = Pose2d(targetPoseVelocity.pos().rotated(-targetPose.heading), targetPoseVelocity.heading)
        val targetRobotPoseAcceleration = Pose2d(targetPoseAcceleration.pos().rotated(-targetPose.heading), targetPoseAcceleration.heading) +
                Pose2d(-targetPoseVelocity.x * Math.sin(targetPose.heading) + targetPoseVelocity.y * Math.cos(targetPose.heading),
                        -targetPoseVelocity.x * Math.cos(targetPose.heading) - targetPoseVelocity.y * Math.sin(targetPose.heading),
                        0.0
                ) * targetPoseVelocity.heading
        val currentRobotPose = Pose2d(currentPose.pos().rotated(-currentPose.heading), currentPose.heading)

        displacementController.targetPosition = targetRobotPose.x
        crossTrackController.targetPosition = targetRobotPose.y

        val axialCorrection = displacementController.update(currentRobotPose.x, targetRobotPoseVelocity.x)
        val headingCorrection = crossTrackController.update(currentRobotPose.y, targetRobotPoseVelocity.heading)

        val correctedVelocity = targetRobotPoseVelocity + Pose2d(axialCorrection, 0.0, headingCorrection)

        val wheelVelocities = TankKinematics.robotToWheelVelocities(correctedVelocity, drive.trackWidth)
        val wheelAccelerations = TankKinematics.robotToWheelAccelerations(targetRobotPoseAcceleration, drive.trackWidth)

        val motorPowers = wheelVelocities
                .zip(wheelAccelerations)
                .map { it.first * kV + it.second * kA }
                .map { it + sign(it) * kStatic }
        drive.setMotorPowers(motorPowers[0], motorPowers[1])
    }
}