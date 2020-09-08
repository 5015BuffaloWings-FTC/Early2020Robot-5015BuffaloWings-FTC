package com.acmerobotics.splinelib

import com.acmerobotics.splinelib.drive.MecanumKinematics
import com.acmerobotics.splinelib.path.heading.SplineInterpolator
import com.acmerobotics.splinelib.path.heading.TangentInterpolator
import com.acmerobotics.splinelib.trajectory.DriveConstraints
import com.acmerobotics.splinelib.trajectory.MecanumConstraints
import com.acmerobotics.splinelib.trajectory.TrajectoryBuilder
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.knowm.xchart.QuickChart


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WheelVelocityTest {
    companion object {
        private const val TRACK_WIDTH = 1.0
        private val BASE_CONSTRAINTS = DriveConstraints(50.0, 25.0, Math.PI / 2, Math.PI / 2)
        private val CONSTRAINTS = MecanumConstraints(BASE_CONSTRAINTS, TRACK_WIDTH)
    }

    @Test
    fun testMecanumWheelVelocityDerivatives() {
        val trajectory = TrajectoryBuilder(Pose2d(0.0, 0.0, 0.0), CONSTRAINTS)
                .splineTo(Pose2d(15.0, 15.0, Math.PI), interpolator = SplineInterpolator(0.0, Math.PI / 2))
                .splineTo(Pose2d(5.0, 35.0, Math.PI / 3), interpolator = TangentInterpolator())
                .build()

        val dt = trajectory.duration() / 10000.0
        val t = (0..10000).map { it * dt }

        val robotVelocities = t.map { Pose2d(trajectory.velocity(it).pos().rotated(-trajectory[it].heading), trajectory.velocity(it).heading) }
        val wheelVelocities = robotVelocities.map { MecanumKinematics.robotToWheelVelocities(it, TRACK_WIDTH) }

        val robotAccelerations = t.map {
            val pose = trajectory[it]
            val poseVel = trajectory.velocity(it)
            val poseAccel = trajectory.acceleration(it)
            Pose2d(
                    poseAccel.x * Math.cos(-pose.heading) - poseVel.x * Math.sin(-pose.heading) * poseVel.heading - poseAccel.y * Math.sin(-pose.heading) - poseVel.y * Math.cos(-pose.heading) * poseVel.heading,
                    poseAccel.x * Math.sin(-pose.heading) + poseVel.x * Math.cos(-pose.heading) * poseVel.heading + poseAccel.y * Math.cos(-pose.heading) - poseVel.y * Math.sin(-pose.heading) * poseVel.heading,
                    poseAccel.heading
            )
        }
        val wheelAccelerations = robotAccelerations.map { MecanumKinematics.robotToWheelAccelerations(it, TRACK_WIDTH) }

        val charts = listOf(
                QuickChart.getChart(
                        "x",
                        "time",
                        "",
                        arrayOf("vel", "accel"),
                        t.toDoubleArray(),
                        arrayOf(robotVelocities.map { it.x }.toDoubleArray(), robotAccelerations.map { it.x }.toDoubleArray())
                ),
                QuickChart.getChart(
                        "y",
                        "time",
                        "",
                        arrayOf("vel", "accel"),
                        t.toDoubleArray(),
                        arrayOf(robotVelocities.map { it.y }.toDoubleArray(), robotAccelerations.map { it.y }.toDoubleArray())
                ),
                QuickChart.getChart(
                        "heading",
                        "time",
                        "",
                        arrayOf("vel", "accel"),
                        t.toDoubleArray(),
                        arrayOf(robotVelocities.map { it.heading }.toDoubleArray(), robotAccelerations.map { it.heading }.toDoubleArray())
                )
        )
//        SwingWrapper(charts).displayChartMatrix()
//
//        Thread.sleep(10000000)

//        val charts = mutableListOf<XYChart>()
        for (i in 0..3) {
            println(i)
            val vel = wheelVelocities.map { it[i] }
            val accel = wheelAccelerations.map { it[i] }

//            val chart = QuickChart.getChart(
//                    "wheel$i",
//                    "time (sec)",
//                    "",
//                    arrayOf("vel", "accel"),
//                    t.toDoubleArray(),
//                    arrayOf(vel.toDoubleArray(), accel.toDoubleArray())
//            )
//            charts.add(chart)
            assertTrue(TestUtil.compareDerivatives(vel, accel, dt, 0.01))
        }
////        SwingWrapper(charts).displayChartMatrix()
    }
}