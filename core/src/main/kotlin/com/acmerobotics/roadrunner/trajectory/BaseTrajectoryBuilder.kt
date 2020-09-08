package com.acmerobotics.roadrunner.trajectory

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.Path
import com.acmerobotics.roadrunner.path.PathBuilder

/**
 * Easy-to-use builder for creating [Trajectory] instances.
 *
 * @param startPose start pose
 * @param trajectory initial trajectory (for splicing)
 * @param t time index in previous trajectory to begin new trajectory
 */
abstract class BaseTrajectoryBuilder protected constructor(
    startPose: Pose2d?,
    startHeading: Double?,
    trajectory: Trajectory?,
    t: Double?
) {
    private var pathBuilder: PathBuilder = if (startPose == null) {
        PathBuilder(trajectory!!.path, trajectory.profile[t!!].x)
    } else {
        PathBuilder(startPose, startHeading!!)
    }

    private val temporalMarkers = mutableListOf<TemporalMarker>()
    private val displacementMarkers = mutableListOf<DisplacementMarker>()
    private val spatialMarkers = mutableListOf<SpatialMarker>()

    /**
     * Adds a line segment with tangent heading interpolation.
     *
     * @param position end position
     */
    fun lineTo(position: Vector2d): BaseTrajectoryBuilder {
        pathBuilder.lineTo(position)

        return this
    }

    /**
     * Adds a line segment with constant heading interpolation.
     *
     * @param position end position
     */
    fun lineToConstantHeading(position: Vector2d): BaseTrajectoryBuilder {
        pathBuilder.lineToConstantHeading(position)

        return this
    }

    /**
     * Adds a line segment with linear heading interpolation.
     *
     * @param position end position
     */
    fun lineToLinearHeading(position: Vector2d, heading: Double): BaseTrajectoryBuilder {
        pathBuilder.lineToLinearHeading(position, heading)

        return this
    }

    /**
     * Adds a line segment with spline heading interpolation.
     *
     * @param position end position
     */
    fun lineToSplineHeading(position: Vector2d, heading: Double): BaseTrajectoryBuilder {
        pathBuilder.lineToSplineHeading(position, heading)

        return this
    }

    /**
     * Adds a strafe path segment.
     *
     * @param position end position
     */
    fun strafeTo(position: Vector2d): BaseTrajectoryBuilder {
        pathBuilder.strafeTo(position)

        return this
    }

    /**
     * Adds a line straight forward.
     *
     * @param distance distance to travel forward
     */
    fun forward(distance: Double): BaseTrajectoryBuilder {
        pathBuilder.forward(distance)

        return this
    }

    /**
     * Adds a line straight backward.
     *
     * @param distance distance to travel backward
     */
    fun back(distance: Double): BaseTrajectoryBuilder {
        pathBuilder.back(distance)

        return this
    }

    /**
     * Adds a segment that strafes left in the robot reference frame.
     *
     * @param distance distance to strafe left
     */
    fun strafeLeft(distance: Double): BaseTrajectoryBuilder {
        pathBuilder.strafeLeft(distance)

        return this
    }

    /**
     * Adds a segment that strafes right in the robot reference frame.
     *
     * @param distance distance to strafe right
     */
    fun strafeRight(distance: Double): BaseTrajectoryBuilder {
        pathBuilder.strafeRight(distance)

        return this
    }

    /**
     * Adds a spline segment with tangent heading interpolation.
     *
     * @param pose end pose
     */
    fun splineTo(pose: Pose2d): BaseTrajectoryBuilder {
        pathBuilder.splineTo(pose)

        return this
    }

    /**
     * Adds a spline segment with constant heading interpolation.
     *
     * @param pose end pose
     */
    fun splineToConstantHeading(pose: Pose2d): BaseTrajectoryBuilder {
        pathBuilder.splineToConstantHeading(pose)

        return this
    }

    /**
     * Adds a spline segment with linear heading interpolation.
     *
     * @param pose end pose
     */
    fun splineToLinearHeading(pose: Pose2d, heading: Double): BaseTrajectoryBuilder {
        pathBuilder.splineToLinearHeading(pose, heading)

        return this
    }

    /**
     * Adds a spline segment with spline heading interpolation.
     *
     * @param pose end pose
     */
    fun splineToSplineHeading(pose: Pose2d, heading: Double): BaseTrajectoryBuilder {
        pathBuilder.splineToSplineHeading(pose, heading)

        return this
    }

    /**
     * Adds a marker to the trajectory at [time].
     */
    fun addTemporalMarker(time: Double, callback: MarkerCallback) =
        addTemporalMarker(0.0, time, callback)

    /**
     * Adds a marker to the trajectory at [scale] * trajectory duration + [offset].
     */
    fun addTemporalMarker(scale: Double, offset: Double, callback: MarkerCallback) =
        addTemporalMarker({ scale * it + offset }, callback)

    /**
     * Adds a marker to the trajectory at [time] evaluated with the trajectory duration.
     */
    fun addTemporalMarker(time: (Double) -> Double, callback: MarkerCallback): BaseTrajectoryBuilder {
        temporalMarkers.add(TemporalMarker(time, callback))

        return this
    }

    /**
     * Adds a marker that will be triggered at the closest trajectory point to [point].
     */
    fun addSpatialMarker(point: Vector2d, callback: MarkerCallback): BaseTrajectoryBuilder {
        spatialMarkers.add(SpatialMarker(point, callback))

        return this
    }

    /**
     * Adds a marker at the current position of the trajectory.
     */
    fun addDisplacementMarker(callback: MarkerCallback) =
        addDisplacementMarker(pathBuilder.build().length(), callback)

    /**
     * Adds a marker to the trajectory at [displacement].
     */
    fun addDisplacementMarker(displacement: Double, callback: MarkerCallback) =
        addDisplacementMarker(0.0, displacement, callback)

    /**
     * Adds a marker to the trajectory at [scale] * path length + [offset].
     */
    fun addDisplacementMarker(scale: Double, offset: Double, callback: MarkerCallback) =
        addDisplacementMarker({ scale * it + offset }, callback)

    /**
     * Adds a marker to the trajectory at [displacement] evaluated with path length.
     */
    fun addDisplacementMarker(displacement: (Double) -> Double, callback: MarkerCallback): BaseTrajectoryBuilder {
        displacementMarkers.add(DisplacementMarker(displacement, callback))

        return this
    }

    /**
     * Constructs the [Trajectory] instance.
     */
    fun build() = buildTrajectory(pathBuilder.build(), temporalMarkers, displacementMarkers, spatialMarkers)

    /**
     * Build a trajectory from [path].
     */
    protected abstract fun buildTrajectory(
        path: Path,
        temporalMarkers: List<TemporalMarker>,
        displacementMarkers: List<DisplacementMarker>,
        spatialMarkers: List<SpatialMarker>
    ): Trajectory
}
