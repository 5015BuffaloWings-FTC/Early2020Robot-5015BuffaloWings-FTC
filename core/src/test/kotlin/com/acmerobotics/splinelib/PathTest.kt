package com.acmerobotics.splinelib

import com.acmerobotics.splinelib.path.Path
import com.acmerobotics.splinelib.path.QuinticSplineSegment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PathTest {
    @Test
    fun testPathDerivatives() {
        val splineSegment = Path(QuinticSplineSegment(
                Waypoint(0.0, 0.0, 20.0, 40.0),
                Waypoint(45.0, 35.0, 60.0, 10.0)
        ))

        val resolution = 1000
        val ds = splineSegment.length() / resolution.toDouble()
        val s = (0..resolution).map { it * ds }

        val x = s.map { splineSegment[it].x }
        val dx = s.map { splineSegment.deriv(it).x }
        val d2x = s.map { splineSegment.secondDeriv(it).x }

        val y = s.map { splineSegment[it].y }
        val dy = s.map { splineSegment.deriv(it).y }
        val d2y = s.map { splineSegment.secondDeriv(it).y }

        val heading = s.map { splineSegment[it].heading }
        val headingDeriv = s.map { splineSegment.deriv(it).heading }
        val headingSecondDeriv = s.map { splineSegment.secondDeriv(it).heading }

        assert(TestUtil.compareDerivatives(x, dx, ds, 0.01))
        assert(TestUtil.compareDerivatives(dx, d2x, ds, 0.01))

        assert(TestUtil.compareDerivatives(y, dy, ds, 0.01))
        assert(TestUtil.compareDerivatives(dy, d2y, ds, 0.01))

        assert(TestUtil.compareDerivatives(heading, headingDeriv, ds, 0.01))
        assert(TestUtil.compareDerivatives(headingDeriv, headingSecondDeriv, ds, 0.01))
    }

    @Test
    fun testProjection() {
//        val line = LineSegment(Vector2d(0.0, 0.0), Vector2d(10.0, 10.0))
        val spline = QuinticSplineSegment(Waypoint(0.0, 0.0, 1.0, 0.0), Waypoint(5.0, 5.0, 1.0, 0.0))
        val path = Path(spline)
        val projectionResult = path.project(Vector2d(2.0, 2.0))
        println(path[projectionResult.displacement].pos())
        println(projectionResult.displacement)
        println(projectionResult.distance)
    }
}