package com.acmerobotics.roadrunner.path

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.heading.ConstantInterpolator
import com.acmerobotics.roadrunner.path.heading.HeadingInterpolator
import com.acmerobotics.roadrunner.path.heading.TangentInterpolator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Easy-to-use builder for creating [Path] instances.
 *
 * @param startPose start pose
 */
class PathBuilder(startPose: Pose2d) {
    private var currentPose: Pose2d = startPose
    private var currentReversed = false

    private var segments = mutableListOf<PathSegment>()

    /**
     * Reverse the direction of robot travel.
     */
    fun reverse(): PathBuilder {
        currentReversed = !currentReversed
        return this
    }

    /**
     * Sets the robot travel direction.
     */
    fun setReversed(reversed: Boolean): PathBuilder {
        this.currentReversed = reversed
        return this
    }

    /**
     * Adds a line path segment.
     *
     * @param pos end position
     * @param interpolator heading interpolator
     */
    @JvmOverloads
    fun lineTo(pos: Vector2d, interpolator: HeadingInterpolator = TangentInterpolator()): PathBuilder {
        val line = if (currentReversed) {
            LineSegment(pos, currentPose.pos())
        } else {
            LineSegment(currentPose.pos(), pos)
        }

        segments.add(PathSegment(line, interpolator, currentReversed))

        currentPose = Pose2d(pos, currentPose.heading)

        return this
    }

    /**
     * Adds a strafe path segment.
     *
     * @param pos end position
     */
    fun strafeTo(pos: Vector2d) = lineTo(pos, ConstantInterpolator(currentPose.heading))

    /**
     * Adds a line straight forward.
     *
     * @param distance distance to travel forward
     */
    fun forward(distance: Double): PathBuilder {
        return lineTo(currentPose.pos() + Vector2d(
            distance * cos(currentPose.heading),
            distance * sin(currentPose.heading)
        )
        )
    }

    /**
     * Adds a line straight backward.
     *
     * @param distance distance to travel backward
     */
    fun back(distance: Double): PathBuilder {
        reverse()
        forward(-distance)
        reverse()
        return this
    }

    /**
     * Adds a segment that strafes left in the robot reference frame.
     *
     * @param distance distance to strafe left
     */
    fun strafeLeft(distance: Double): PathBuilder {
        return strafeTo(currentPose.pos() + Vector2d(
            distance * cos(currentPose.heading + PI / 2),
            distance * sin(currentPose.heading + PI / 2)
        )
        )
    }

    /**
     * Adds a segment that strafes right in the robot reference frame.
     *
     * @param distance distance to strafe right
     */
    fun strafeRight(distance: Double): PathBuilder {
        return strafeLeft(-distance)
    }

    /**
     * Adds a spline segment.
     *
     * @param pose end pose
     * @param interpolator heading interpolator
     */
    @JvmOverloads
    fun splineTo(pose: Pose2d, interpolator: HeadingInterpolator = TangentInterpolator()): PathBuilder {
        val derivMag = (currentPose.pos() distanceTo pose.pos())
        val startWaypoint = QuinticSpline.Waypoint(currentPose.x, currentPose.y,
            derivMag * cos(currentPose.heading), derivMag * sin(currentPose.heading))
        val endWaypoint = QuinticSpline.Waypoint(pose.x, pose.y,
            derivMag * cos(pose.heading), derivMag * sin(pose.heading))

        val spline = if (currentReversed) {
            QuinticSpline(endWaypoint, startWaypoint)
        } else {
            QuinticSpline(startWaypoint, endWaypoint)
        }

        segments.add(PathSegment(spline, interpolator, currentReversed))

        currentPose = pose

        return this
    }

    /**
     * Constructs the [Path] instance.
     */
    fun build() = Path(segments)
}
