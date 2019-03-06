package com.example.citygame

import android.app.Activity
import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.thread
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    lateinit var activity : Activity
    lateinit var thisCity : City
    lateinit var timer : Timer
    var addedButtons = mutableMapOf<String,Button>()

    private fun showEndGameAlert(message: String) {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(message)
        builder.setCancelable(false)
        builder.setNeutralButton("Try again!"){ _,_ -> startGame()}
        builder.create().let { it.show() }

    }

    private fun refreshData() {
        foodTextView.text = "Food: ${thisCity.food}"
        woodTextView.text = "Wood: ${thisCity.wood}"
        clayTextView.text = "Clay: ${thisCity.clay}"
        ironTextView.text = "Iron: ${thisCity.iron}"
        populationTextView.text = "Population: ${thisCity.population}"
        manageNewOptions()
    }

    private fun manageNewOptions() {
        if (thisCity.canBuild(Building.GATHERERS_HUT)) {
            addedButtons.getValue("BUILD GATHERER'S HUT").visibility = View.VISIBLE
        } else {
            addedButtons.getValue("BUILD GATHERER'S HUT").visibility = View.GONE
        }
        if (thisCity.canBuild(Building.SAWMILL)) {
            addedButtons.getValue("BUILD SAWMILL").visibility = View.VISIBLE
        } else {
            addedButtons.getValue("BUILD SAWMILL").visibility = View.GONE
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this
        startGame()
        addNewButton("BUILD GATHERER'S HUT") {thisCity.buildSomething(Building.GATHERERS_HUT)}
        addNewButton("BUILD SAWMILL") {thisCity.buildSomething(Building.SAWMILL)}
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }
    override fun onResume() {
        super.onResume()
        timer = timer(daemon = true,period = 1000) {
            activity.runOnUiThread { refreshData() }
        }
    }

    private fun startGame() {
        thisCity = City(1000, 0.4, {deadCityHandler()}, ::newBuildingHandler).apply { start() }
    }

    private fun deadCityHandler() {
        activity.runOnUiThread {
            if (!activity.isFinishing)
                showEndGameAlert("GAME OVER!")
        }
    }

    private fun newBuildingHandler(building: Building) {
        imageView.setImageResource(R.drawable.city_gatherer)
    }

    fun cutTreesClicked(view: View) {
        thisCity.wood++
        woodTextView.text = "Wood: ${thisCity.wood}"
    }

    fun gatherFoodClicked(view: View) {
        thisCity.food++
        foodTextView.text = "Food: ${thisCity.food}"
    }

    fun gatherClayClicked(view: View) {
        thisCity.clay++
        clayTextView.text = "Clay: ${thisCity.clay}"
    }

    private fun addNewButton(caption: String, action: () -> Unit) {
        val exists = (1 until buttons.childCount).any { i ->
            buttons.getChildAt(i).let {
                it is Button && it.text == caption
            }
        }
        if (!exists) {
            val button = Button(this).apply { this.text = caption; this.setOnClickListener { action() } }
            buttons.addView(button)
            addedButtons[caption] = button
        }
    }
}
