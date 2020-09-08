motion-planner
==============
A simple Kotlin library for planning 2D mobile robot paths and trajectories designed for FTC.

# Features

## Core
- [x] Quintic ~~bezier~~ splines
- [x] Point turns
- [x] Linear segments
- [x] Dynamic constraint-capable trapezoidal motion profiling
- [x] Heading interpolators
- [ ] Modifiers for common FTC drivetrains
- [ ] Nice DSL/builder
- [ ] Spline optimizer
- [ ] Various spline followers (PID, pure pursuit, time-varying non-linear feedback, gvf, etc.)
- [ ] Feedforward/drivetrain parameter tuning routines
- [ ] Localization routines?

## GUI
- [ ] Path view
- [ ] Waypoint list
- [ ] Interactive waypoint dragging
- [ ] Curvature/profile visualization

## Plugin
- [ ] Path serialization/loading
- [ ] Live positional feedback