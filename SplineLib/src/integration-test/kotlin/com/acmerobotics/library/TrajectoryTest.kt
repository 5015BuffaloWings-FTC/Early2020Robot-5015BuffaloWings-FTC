package com.acmerobotics.library

import com.acmerobotics.library.path.parametric.QuinticSpline
import com.acmerobotics.library.trajectory.DriveConstraints
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrajectoryTest {
    @Test
    fun testSimpleSpline() {
        val constraints = DriveConstraints(
            50.0,
            25.0,
            maximumAngularVelocity = 2 * Math.PI,
            maximumAngularAcceleration = Math.PI
        )
        val spline = QuinticSpline.fromPoses(
            Pose2d(0.0, 0.0, Math.PI / 6),
            Pose2d(108.0, 72.0, 3 * Math.PI / 4),
            Pose2d(24.0, 32.0, -Math.PI)
        )
        for (splineSegment in spline) {
            println(splineSegment)
            println(splineSegment.length())
        }
//        val trajectory = Trajectory(
//            listOf(
//                PathTrajectorySegment(
//                    spline.map { Path(it) },
//                    spline.map { constraints },
//                    10000
//                )
//            )
//        )
//        GraphUtil.saveTrajectory("simpleSpline", trajectory)
//        GraphUtil.saveMotionProfile("simpleSpline", (trajectory.segments[0] as PathTrajectorySegment).profile, false)
//        GraphUtil.saveParametricCurve("simpleSpline", spline)
//        CSVUtil.savePath("simpleSpline", path)
    }
}