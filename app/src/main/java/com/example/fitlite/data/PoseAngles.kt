package com.example.fitlite.data

import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark
import java.lang.Math.toDegrees
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.sqrt

data class PoseAngles(
    val leftElbowAngle: Double? = null,
    val rightElbowAngle: Double? = null,
    val leftShoulderAngle: Double? = null,
    val rightShoulderAngle: Double? = null,
    val leftKneeAngle: Double? = null,
    val rightKneeAngle: Double? = null,
    val leftHipAngle: Double? = null,
    val rightHipAngle: Double? = null,
    val leftAnkleAngle: Double? = null,
    val rightAnkleAngle: Double? = null,
    val neckAngle: Double? = null,
    val spineAngle: Double? = null
)

fun extractPoseAngles2D(landmarks: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>): PoseAngles {
    fun get(index: Int) = landmarks.getOrNull(index)

    fun calculateAngle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Double {
        val v1x = x1 - x2
        val v1y = y1 - y2
        val v2x = x3 - x2
        val v2y = y3 - y2
        val dot = v1x * v2x + v1y * v2y
        val mag1 = sqrt((v1x * v1x + v1y * v1y).toDouble())
        val mag2 = sqrt((v2x * v2x + v2y * v2y).toDouble())
        return toDegrees(acos((dot / (mag1 * mag2)).coerceIn(-1.0, 1.0)))
    }

    fun angle(a: Int, b: Int, c: Int): Double? {
        val p1 = get(a) ?: return null
        val p2 = get(b) ?: return null
        val p3 = get(c) ?: return null
        return calculateAngle(p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y())
    }

    return PoseAngles(
        leftElbowAngle = angle(11, 13, 15),
        rightElbowAngle = angle(12, 14, 16),
        leftShoulderAngle = angle(13, 11, 23),
        rightShoulderAngle = angle(14, 12, 24),
        leftKneeAngle = angle(23, 25, 27),
        rightKneeAngle = angle(24, 26, 28),
        leftHipAngle = angle(11, 23, 25),
        rightHipAngle = angle(12, 24, 26)
    )
}

fun getPushUpAngleChecks(angles: PoseAngles): List<Boolean> {
    return listOf(
        angles.leftElbowAngle?.let { it in 160.0..180.0 } == true,
        angles.rightElbowAngle?.let { it in 160.0..180.0 } == true,
        angles.leftHipAngle?.let { it in 160.0..180.0 } == true,
        angles.rightHipAngle?.let { it in 160.0..180.0 } == true
    )
}

fun getSquatAngleChecks(angles: PoseAngles): List<Boolean> {
    return listOf(
        angles.leftKneeAngle?.let { it in 60.0..100.0 } == true,
        angles.rightKneeAngle?.let { it in 60.0..100.0 } == true,
        angles.leftHipAngle?.let { it in 60.0..100.0 } == true,
        angles.rightHipAngle?.let { it in 60.0..100.0 } == true
    )
}