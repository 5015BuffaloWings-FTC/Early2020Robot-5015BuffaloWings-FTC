package com.acmerobotics.splinelib.profile

/**
 * Segment of a motion profile with constant acceleration.
 *
 * @param start start motion state
 * @param dt time delta
 */
class MotionSegment(val start: MotionState, val dt: Double) {

    /**
     * Returns the [MotionState] at time [t].
     */
    operator fun get(t: Double) = start[t]

    /**
     * Returns the [MotionState] at the end of the segment (time [dt]).
     */
    fun end() = start[dt]

    /**
     * Returns a reversed version of the profile.
     */
    fun reversed(): MotionSegment {
        val end = end()
        val state = MotionState(end.x, -end.v, end.a)
        return MotionSegment(state, dt)
    }
}