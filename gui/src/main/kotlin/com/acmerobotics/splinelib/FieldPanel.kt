package com.acmerobotics.splinelib

import com.acmerobotics.splinelib.trajectory.Trajectory
import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.io.File
import java.lang.reflect.Field
import javax.imageio.ImageIO
import javax.swing.JPanel
import kotlin.math.min
import kotlin.math.roundToInt
import javax.swing.ImageIcon



class FieldPanel : JPanel() {
    companion object {
        const val RESOLUTION = 1000
    }

    var trajectory = Trajectory()

    init {
        preferredSize = Dimension(500, 500)
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)

        val g2d = g as Graphics2D

        // antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

        // transform coordinate frame
        val fieldSize = min(width, height)
        val offsetX = (width - fieldSize) / 2.0
        val offsetY = (height - fieldSize) / 2.0

        val transform = AffineTransform()
        transform.translate(width / 2.0, height / 2.0)
        transform.scale(fieldSize / 144.0, fieldSize / 144.0)
        transform.rotate(Math.PI / 2)
        transform.scale(-1.0, 1.0)

        // draw field
        val fieldImage = ImageIO.read(javaClass.getResource("/transparent_field.png"));
        g2d.drawImage(fieldImage, offsetX.roundToInt(), offsetY.roundToInt(), fieldSize, fieldSize, null)

        // draw spline
        g2d.stroke = BasicStroke(5F)
        g2d.color = Color(76, 175, 80)
        val displacements = (0..RESOLUTION).map { it / RESOLUTION.toDouble() * trajectory.duration() }
        for (i in 1..RESOLUTION) {
            val firstRawPoint = trajectory[displacements[i-1]]
            val secondRawPoint = trajectory[displacements[i]]
            val firstPoint = Point2D.Double()
            val secondPoint = Point2D.Double()
            transform.transform(Point2D.Double(firstRawPoint.x, firstRawPoint.y), firstPoint)
            transform.transform(Point2D.Double(secondRawPoint.x, secondRawPoint.y), secondPoint)
            g2d.drawLine(firstPoint.x.roundToInt(), firstPoint.y.roundToInt(), secondPoint.x.roundToInt(), secondPoint.y.roundToInt())
        }
    }
}