package com.acmerobotics.library

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.QuickChart
import org.knowm.xchart.XYChart
import java.io.File

object GraphUtil {
    private const val GRAPH_DIR = "./graphs/"
    private const val GRAPH_DPI = 300

    private fun saveGraph(name: String, graph: XYChart) {
        File(GRAPH_DIR).mkdirs()

        BitmapEncoder.saveBitmapWithDPI(graph, "$GRAPH_DIR$name", BitmapEncoder.BitmapFormat.PNG, GRAPH_DPI)
    }

    fun saveMotionProfile(name: String, profile: MotionProfile, includeAcceleration: Boolean = true, resolution: Int = 1000) {
        val timeData = (0..resolution).map { it / resolution.toDouble() * profile.duration() }.toDoubleArray()
        val positionData = timeData.map { profile[it].x }.toDoubleArray()
        val velocityData = timeData.map { profile[it].v }.toDoubleArray()

        val labels = mutableListOf("x(t)", "v(t)")
        val data = mutableListOf(positionData, velocityData)

        if (includeAcceleration) {
            val accelerationData = timeData.map { profile[it].a }.toDoubleArray()

            labels.add("a(t)")
            data.add(accelerationData)
        }

        saveGraph("${name}Profile", QuickChart.getChart(
            name,
            "time (sec)",
            "",
            labels.toTypedArray(),
            timeData,
            data.toTypedArray()
        ))
    }

    fun savePath(name: String, path: Path, resolution: Int = 1000) {
        val displacementData = (0..resolution).map { it / resolution.toDouble() * path.length() }
        val points = displacementData.map { path[it] }
        val xData = points.map { it.x }.toDoubleArray()
        val yData = points.map { it.y }.toDoubleArray()

        val graph = QuickChart.getChart(name, "x", "y", "path", xData, yData)
        graph.styler.isLegendVisible = false
        saveGraph("${name}Path", graph)
    }

    fun saveTrajectory(name: String, trajectory: Trajectory, resolution: Int = 1000) {
        val timeData = (0..resolution).map { it / resolution.toDouble() * trajectory.duration() }.toDoubleArray()
        val velocityData = timeData.map { trajectory.velocity(it) }
        val xVelocityData = velocityData.map { it.x }.toDoubleArray()
        val yVelocityData = velocityData.map { it.y }.toDoubleArray()
        val omegaData = velocityData.map { it.heading }.toDoubleArray()

        val labels = listOf("v_x(t)", "v_y(t)", "ω(t)")
        val data = listOf(xVelocityData, yVelocityData, omegaData)

        saveGraph("${name}Trajectory", QuickChart.getChart(
            name,
            "time (sec)",
            "",
            labels.toTypedArray(),
            timeData,
            data.toTypedArray()
        ))
    }
}