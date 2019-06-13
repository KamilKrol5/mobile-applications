package com.example.pong

import android.graphics.Canvas
import android.view.SurfaceHolder
import java.lang.Exception

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
    private val fps = 60
    private var canvas: Canvas? = null
    var running = true

    override fun run() {
        var startTime: Long
        var time: Long
        var wait: Long
        val target = (1000 / fps).toLong()

        while (running) {
            startTime = System.nanoTime()
            canvas = null

            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameView.update()
                    gameView.draw(canvas!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            time = (System.nanoTime() - startTime) / 1000000
            wait = target - time
            wait = if (wait < 0) 0 else wait

            try {
                sleep(wait)
            } catch (e : Exception) {e .printStackTrace()}
        }
    }
}