package com.example.pong

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context),
    SurfaceHolder.Callback {
    var canLeft = true
    var canRight = true
    private val thread: GameThread
    private lateinit var callback : PointCounter


    private var ball = Ball(20f, 0f, 0f)
    private val lPaddle = Paddle(0f, 0f, 0f)
    private val rPaddle = Paddle(0f, 0f, 0f)

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
    }

    fun update() {
        ball.update()
        if (ball.x <= lPaddle.x + lPaddle.getWidth() && ball.y <= lPaddle.y + lPaddle.height && ball.y >= lPaddle.y) {
            if (canLeft) {
                ball.randomizedBounce(0.2f)
                canLeft = false
                canRight = true
            }
        }

        else if (ball.x + ball.getSize() >= rPaddle.x && ball.y <= rPaddle.y + rPaddle.height && ball.y >= rPaddle.y) {
            if (canRight) {
                ball.randomizedBounce(0.2f)
                canLeft = true
                canRight = false
            }
        }

        else if (ball.x <= 0) {
            callback.onPointCount(true)
            nextRound()
        }
        else if (ball.x + ball.getSize() >= width) {
            callback.onPointCount(false)
            nextRound()
        }
        if (ball.y <= 0 || ball.y + ball.getSize() >= height){
            ball.bounce(false)
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        if (canvas == null) return

        ball.draw(canvas)
        lPaddle.draw(canvas)
        rPaddle.draw(canvas)
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        lPaddle.height = height / 3.0f
        rPaddle.height = height / 3.0f
        lPaddle.x = 0.0f
        rPaddle.x = width - rPaddle.getWidth()

        nextRound()

        thread.running = true
        thread.start()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        for (i in 0 until event!!.pointerCount) {
            if (event.getX(i) < width / 2f) {
                lPaddle.y = event.getY(i) - lPaddle.height / 2
            } else {
                rPaddle.y = event.getY(i) - rPaddle.height / 2
            }
        }
        return true
    }

    private fun nextRound() {
        ball.y = height / 2.0f
        ball.x = width / 2.0f
        ball.resetSpeed()
        rPaddle.y = height / 2.0f - rPaddle.height / 2
        lPaddle.y = height / 2.0f - lPaddle.height / 2
        canLeft = true
        canRight = true
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        thread.running = false
        thread.join()
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

    fun setOnPointCounter(pointCounter: PointCounter) {
        callback = pointCounter
    }

    interface PointCounter {
        fun onPointCount(left: Boolean)
    }
}