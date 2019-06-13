package com.example.pong

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_main.*

const val PREFERENCES_NAME = "pong"
class GameActivity : AppCompatActivity(), GameView.PointCounter {
    private var best = 0
    private var leftPoints = 0
    private var rightPoints = 0
    private lateinit var preferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE)
        preferences.edit().putInt("right", rightPoints).putInt("left", leftPoints).apply()
        best = preferences.getInt("best", 0)
        bestText.text = "Best: $best"
        val game = GameView(this)
        game.setOnPointCounter(this)
        game.elevation = -10f
        container.addView(game)
    }

    override fun onPointCount(left: Boolean) {
        if (left) {
            leftPoints++
            preferences.edit().putInt("left", leftPoints).apply()
        }  else {
            rightPoints++
            preferences.edit().putInt("right", rightPoints).apply()
        }

        if (preferences.getInt("best", 0) < leftPoints) {
            preferences.edit().putInt("best", leftPoints).apply()
            best = leftPoints
        } else if (preferences.getInt("best", 0) < rightPoints) {
            preferences.edit().putInt("best", rightPoints).apply()
            best = rightPoints
        }
//        Log.i("SPREF","L:" + preferences.getInt("left", -1000).toString())
//        Log.i("SPREF","R:" + preferences.getInt("right", -1000).toString())

        runOnUiThread { scoreText.text = "$leftPoints : $rightPoints" }
        runOnUiThread { bestText.text = "Best: $best" }
    }

    override fun onRestart() {
        super.onRestart()
        setContentView(R.layout.activity_main)

        playButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }
}
