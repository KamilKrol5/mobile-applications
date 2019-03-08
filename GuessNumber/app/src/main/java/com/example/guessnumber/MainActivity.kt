package com.example.guessnumber

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    var numberOfGuesses = 0
    var gameStarted = false
    var upperBound = 50
    var number = Random.nextInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun checkClicked(view: View) {
        if (gameStarted) {
            if (editText.text.isEmpty()) {
                infoTextView.text = "Type a number!"
            } else {
                numberOfGuesses++
                editText.text.toString().toInt().let {
                    when {
                        it == number -> {
                            endGame("You have won! Your number is $number \nNumber of guesses: $numberOfGuesses")
                        }
                        it < number -> infoTextView.text = "Your number is bigger than $it"
                        else -> infoTextView.text = "Your number is smaller than $it"
                    }
                    editText.setText("")
                }
            }
        } else {
            startGame()
        }
    }

    private fun endGame(message: String) {
        gameStarted = false
        editText.visibility = View.INVISIBLE
        button.text = "START"
        infoTextView.text = message
    }

    private fun startGame() {
        gameStarted = true
        numberOfGuesses = 0
        editText.visibility = View.VISIBLE
        number = (0..upperBound).random()
        infoTextView.text = "Try to guess your number"
        button.text = "CHECK"
    }

    fun optionsClicked(view: View) {
        if (scrollView.visibility == View.VISIBLE) {
            scrollView.visibility = View.INVISIBLE
            button.visibility = View.VISIBLE
        } else {
            scrollView.visibility = View.VISIBLE
            button.visibility = View.INVISIBLE
        }
    }

    fun rangeChooseClick(view: View) {
        when (view) {
            radioButton025 -> upperBound = 25
            radioButton050 -> upperBound = 50
            radioButton0100 -> upperBound =100
        }
        endGame("")
    }
    fun closeOptions(view: View) {
        scrollView.visibility = View.INVISIBLE
        button.visibility = View.VISIBLE
    }
}
