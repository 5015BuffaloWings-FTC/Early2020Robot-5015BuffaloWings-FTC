package com.acmerobotics.roadrunner.trajectory

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.LineSegment
import com.acmerobotics.roadrunner.path.Path
import com.acmerobotics.roadrunner.path.QuinticSpline
import com.acmerobotics.roadrunner.path.heading.ConstantInterpolator
import com.acmerobotics.roadrunner.path.heading.HeadingInterpolator
import com.acmerobotics.roadrunner.path.heading.TangentInterpolator
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryConstraints
import com.acmerobotics.roadrunner.util.Angle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Easy-to-use builder for creating [Trajectory] instances.
 *
 * @param startPose start pose
 * @param globalConstraints global drive constraints (overridable for specific segments)
 * @param resolution resolution used for path-based segments (see [PathTrajectorySegment])
 */
class TrajectoryBuilder @JvmOverloads constructor(
    startPose: Pose2d,
    private val globalConstraints: DriveConstraints,
    private val resolution: Double = 0.25
) {
    private var currentPose: Pose2d = startPose
    private val trajectorySegments = mutableListOf<TrajectorySegment>()
    private var paths = mutableListOf<Path>()
    private var constraintsList = mutableListOf<TrajectoryConstraints>()
    private var composite = false
    private var reversed = false

    /**
     * Reverse the direction of robot travel.
     */
    fun reverse(): TrajectoryBuilder {
        reversed = !reversed
        return this
    }

    /**
     * Sets the robot travel direction.
     */
    fun setReversed(reversed: Boolean): TrajectoryBuilder {
        this.reversed = reversed
        return this
    }

    /**
     * Adds a point turn.
     *
     * @param angle angle to turn by (relative to the current heading)
     * @param constraintsOverride turn-specific drive constraints
     */
    @JvmOverloads
    fun turn(angle: Double, constraintsOverride: DriveConstraints? = null): TrajectoryBuilder {
        if (composite) {
            closeComposite()
        }

        val constraints = constraintsOverride ?: globalConstraints
        val pointTurn = PointTurn(currentPose, angle, constraints)
        trajectorySegments.add(pointTurn)
        currentPose = Pose2d(currentPose.pos(), Angle.norm(currentPose.heading + angle))

        return this
    }

    /**
     * Adds a point turn.
     *
     * @param heading heading to turn to
     * @param constraintsOverride turn-specific drive constraints
     */
    @JvmOverloads
    fun turnTo(heading: Double, constraintsOverride: DriveConstraints? = null): TrajectoryBuilder {
        val ccwTurnAngle = Angle.norm(heading - currentPose.heading)
        val turnAngle = if (ccwTurnAngle <= PI) {
            ccwTurnAngle
        } else {
            Angle.norm(currentPose.heading - heading)
        }

        return turn(turnAngle, constraintsOverride)
    }

    /**
     * Adds a line path segment.
     *
     * @param pos end position
     * @param interpolator heading interpolator
     * @param constraintsOverride line-specific drive constraints
     */
    @JvmOverloads
    fun lineTo(
        pos: Vector2d,
        interpolator: HeadingInterpolator = TangentInterpolator(),
        constraintsOverride: TrajectoryConstraints? = null
    ): TrajectoryBuilder {
        val postBeginComposite = if (!interpolator.respectsDerivativeContinuity() && composite) {
            closeComposite()
            true
        } else {
            false
        }

        val constraints = constraintsOverride ?: globalConstraints
        val line = if (reversed) {
            Path(LineSegment(pos, currentPose.pos()), interpolator, true)
        } else {
            Path(LineSegment(currentPose.pos(), pos), interpolator, false)
        }
        if (composite) {
            paths.add(line)
            constraintsList.add(constraints)
        } else {
            trajectorySegments.add(PathTrajectorySegment(listOf(line), listOf(constraints), resolution))
        }
        currentPose = Pose2d(pos, currentPose.heading)

        if (postBeginComposite) {
            beginComposite()
        }

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
    fun forward(distance: Double): TrajectoryBuilder {
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
    fun back(distance: Double): TrajectoryBuilder {
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
    fun strafeLeft(distance: Double): TrajectoryBuilder {
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
    fun strafeRight(distance: Double): TrajectoryBuilder {
        return strafeLeft(-distance)
    }

    /**
     * Adds a spline segment.
     *
     * @param pose end pose
     * @param interpolator heading interpolator
     * @param constraintsOverride spline-specific constraints
     */
    @JvmOverloads
    fun splineTo(
        pose: Pose2d,
        interpolator: HeadingInterpolator = TangentInterpolator(),
        constraintsOverride: TrajectoryConstraints? = null
    ): TrajectoryBuilder {
        val postBeginComposite = if (!interpolator.respectsDerivativeContinuity() && composite) {
            closeComposite()
            true
        } else {
            false
        }

        val constraints = constraintsOverride ?: this.globalConstraints
        val derivMag = (currentPose.pos() distanceTo pose.pos())
        val startWaypoint = QuinticSpline.Waypoint(currentPose.x, currentPose.y,
            derivMag * cos(currentPose.heading), derivMag * sin(currentPose.heading))
        val endWaypoint = QuinticSpline.Waypoint(pose.x, pose.y,
            derivMag * cos(pose.heading), derivMag * sin(pose.heading))

        val spline = if (reversed) {
            Path(QuinticSpline(endWaypoint, startWaypoint), interpolator, true)
        } else {
            Path(QuinticSpline(startWaypoint, endWaypoint), interpolator, false)
        }
        if (composite) {
            paths.add(spline)
            constraintsList.add(constraints)
        } else {
            trajectorySegments.add(PathTrajectorySegment(listOf(spline), listOf(constraints), resolution))
        }
        currentPose = pose

        if (postBeginComposite) {
            beginComposite()
        }

        return this
    }

    /**
     * Adds a wait segment.
     *
     * @param duration wait duration
     */
    fun waitFor(duration: Double): TrajectoryBuilder {
        trajectorySegments.add(WaitSegment(currentPose, duration))
        return this
    }

    /**
     * Begins a composite path trajectory segment backed by a single continuous profile.
     */
    fun beginComposite(): TrajectoryBuilder {
        composite = true
        return this
    }

    /**
     * Closes a composite path trajectory segment (see [beginComposite]).
     */
    fun closeComposite(): TrajectoryBuilder {
        composite = false
        if (paths.isNotEmpty() && constraintsList.isNotEmpty()) {
            trajectorySegments.add(PathTrajectorySegment(paths, constraintsList, resolution))
            paths = mutableListOf()
            constraintsList = mutableListOf()
        }
        return this
    }

    /**
     * Constructs the [Trajectory] instance.
     */
    fun build(): Trajectory {
        if (composite) {
            closeComposite()
        }
        return Trajectory(trajectorySegments)
    }
}
