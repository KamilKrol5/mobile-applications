package com.example.pong

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.math.MathUtils
import kotlin.math.cos
import kotlin.math.sign
import kotlin.random.Random

const val SIZE = 70f

class Ball(private val speed: Float, var x: Float, var y: Float) {
    private val color = Paint().also { it.setARGB(255, 21, 100, 12 * 16) }

    private var xSpeed = 0f
    private var ySpeed = 0f

    fun resetSpeed() {
        val angle = Random.nextDouble(2 * Math.PI).toFloat()
        ySpeed = 0.9f * speed * cos(angle) * randomNegativity()
        xSpeed = Math.sqrt((speed.toDouble()) * (speed.toDouble()) - ySpeed * ySpeed).toFloat() * randomNegativity()
    }

    init {
        resetSpeed()
    }

    fun update() {
        x += xSpeed
        y += ySpeed
    }

    fun bounce(side: Boolean) {
        if (side) xSpeed = -xSpeed
        else ySpeed = -ySpeed
    }

    fun randomizedBounce(randomnessLevel: Float) {
        val r = Random.nextFloat() % randomnessLevel * randomNegativity()
        xSpeed *= -(1 + r)
        xSpeed = MathUtils.clamp(xSpeed, -speed, speed)
        ySpeed = ySpeed.sign * Math.sqrt(speed * speed - xSpeed.toDouble() * xSpeed.toDouble()).toFloat()

    }

    fun draw(canvas: Canvas) {
        canvas.drawOval(RectF(x, y, x + SIZE, y + SIZE), color)
    }

    private fun randomNegativity(): Int {
        return Math.pow((-1).toDouble(), Random.nextInt(2).toDouble()).toInt()
    }

    fun getSize(): Float {
        return SIZE
    }

}
