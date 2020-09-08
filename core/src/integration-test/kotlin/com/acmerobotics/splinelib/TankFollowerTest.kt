package com.acmerobotics.splinelib

import com.acmerobotics.splinelib.control.PIDCoefficients
import com.acmerobotics.splinelib.drive.TankDrive
import com.acmerobotics.splinelib.followers.TankPIDVAFollower
import com.acmerobotics.splinelib.trajectory.DriveConstraints
import com.acmerobotics.splinelib.trajectory.TankConstraints
import com.acmerobotics.splinelib.trajectory.TrajectoryBuilder
import org.apache.commons.math3.distribution.NormalDistribution
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.knowm.xchart.XYChart
import org.knowm.xchart.style.markers.None
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TankFollowerTest {
    companion object {
        const val kV = 1.0 / 60.0
        const val SIMULATION_HZ = 25
        const val TRACK_WIDTH = 3.0

        private val BASE_CONSTRAINTS = DriveConstraints(50.0, 25.0, Math.PI / 2, Math.PI / 2)
        private val CONSTRAINTS = TankConstraints(BASE_CONSTRAINTS, TRACK_WIDTH)
    }

    private class SimulatedTankDrive(
            private val dt: Double,
            private val kV: Double,
            trackWidth: Double
    ) : TankDrive(trackWidth) {
        companion object {
//            val VOLTAGE_NOISE_DIST = NormalDistribution(0.0, 0.25 / 12.0)
            val VOLTAGE_NOISE_DIST = NormalDistribution(1.0, 0.05)

            fun clamp(value: Double, min: Double, max: Double) = min(max, max(min, value))
        }

        var powers = listOf(0.0, 0.0)
        var positions = listOf(0.0, 0.0)

        override fun setMotorPowers(left: Double, right: Double) {
            powers = listOf(left, right)
                    .map { it * VOLTAGE_NOISE_DIST.sample() }
                    .map { clamp(it, 0.0, 1.0) }
        }

        override fun getMotorPositions(): List<Double> = positions

        override fun updatePoseEstimate(timestamp: Double) {
            positions = positions.zip(powers)
                    .map { it.first + it.second / kV * dt }
            super.updatePoseEstimate(timestamp)
        }
    }

    @Test
    fun simulatePIDVAFollower() {
        val dt = 1.0 / SIMULATION_HZ

        val trajectory = TrajectoryBuilder(Pose2d(0.0, 0.0, 0.0), CONSTRAINTS)
                .beginComposite()
                .splineTo(Pose2d(15.0, 15.0, Math.PI))
                .splineTo(Pose2d(5.0, 35.0, Math.PI / 3))
                .closeComposite()
                .waitFor(0.5)
                .build()

        val drive = SimulatedTankDrive(dt, kV, TRACK_WIDTH)
        val follower = TankPIDVAFollower(drive, PIDCoefficients(1.0), PIDCoefficients(kP = 5.0, kD = 2.0), kV, 0.0, 0.0)
        follower.followTrajectory(trajectory, 0.0)

        val targetPositions = mutableListOf<Vector2d>()
        val actualPositions = mutableListOf<Vector2d>()

        drive.resetPoseEstimate(trajectory.start())
        val samples = ceil(trajectory.duration() / dt).toInt()
        for (sample in 0..samples) {
            val t = sample * dt
            follower.update(drive.getPoseEstimate(), t)
            drive.updatePoseEstimate(t)

            targetPositions.add(trajectory[t].pos())
            actualPositions.add(drive.getPoseEstimate().pos())
        }

        val graph = XYChart(600, 400)
        graph.title = "Tank PIDVA Follower Sim"
        graph.addSeries(
                "Target Trajectory",
                targetPositions.map { it.x }.toDoubleArray(),
                targetPositions.map { it.y }.toDoubleArray())
        graph.addSeries(
                "Actual Trajectory",
                actualPositions.map { it.x }.toDoubleArray(),
                actualPositions.map { it.y }.toDoubleArray())
        graph.seriesMap.values.forEach { it.marker = None() }
        GraphUtil.saveGraph("tankSim", graph)
    }
}
