package com.example.hangman


import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TableRow
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.end_game_dialog.*

class MainActivity : AppCompatActivity() {

    private var lettersButtons = mutableMapOf<String,Button>()
    lateinit var word : String
    var attempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        var inflater = layoutInflater
//        var keyboard = inflater.inflate(R.layout.keyboard_eng,null)
//        keyboardTable.addView(keyboard)

        lateinit var currentRow : TableRow
        for ((counter, l) in resources.getStringArray(R.array.letters).withIndex()) {
            if (counter.rem(7) == 0) {
                currentRow = TableRow(this)
                val params = TableRow.LayoutParams()
                params.width = TableRow.LayoutParams.MATCH_PARENT
                currentRow.layoutParams = params
                currentRow.gravity = Gravity.CENTER
                keyboardTable.addView(currentRow)
            }

            currentRow.addView(Button(this).apply {
                text = l
                val params = TableRow.LayoutParams()
                params.width = 200
                params.setMargins(1,1,1,1)
                this.layoutParams = params
                setOnClickListener { keyboardButtonClicked(this, l.toLowerCase()) }
                lettersButtons[l] = this
            })
        }

        startGame()
    }

    private fun startGame() {
        var counter = 0
        var randomLine = -1
        attempts = 0
        imageView.setImageResource(R.drawable.hangman2)
        lettersButtons.forEach { _,v ->
            v.isClickable = true
            v.setBackgroundColor(getColor(R.color.keyboardBackground))
        }
        resources.openRawResource(R.raw.hangman_words).bufferedReader().forEachLine {
            if (counter == 0) {
                val amountOfLines = it.toInt()
                randomLine = (1..amountOfLines).shuffled().random()
            } else if (counter == randomLine) {
                Log.i("xD","$counter, $randomLine, $it")
                word = it
                counter++
                return@forEachLine
            }
            counter++
        }
        if (!::word.isInitialized) {
            error("Something went wrong while choosing the word")
        }
        wordTextView.text = word.replace(regex = Regex("(.)"),replacement = "?")
        Log.i("xD","${wordTextView.text.length}")
    }

    private fun keyboardButtonClicked(button: Button,letter: String) {
        Log.i("xD",letter)
        if (checkLetterInWord(letter))
            button.setBackgroundColor(getColor(R.color.letterChosenGood))
        else
            button.setBackgroundColor(getColor(R.color.letterChosenBad))
        button.isClickable = false
    }

    private fun checkLetterInWord(letter: String): Boolean {
        return if (word.contains(letter)) {
            for (i in 0 until word.length) {
                if (word[i] == letter[0]) {
                    wordTextView.text = wordTextView.text.replaceRange(i,i+1,letter)
                }
            }
            true
        } else {
            attempts++
            redrawHangman()
            false
        }
    }

    private fun redrawHangman() {
        if (attempts+2 <= 11)
            imageView.setImageResource(resources.getIdentifier("hangman"+(attempts+2),"drawable",packageName))
        if (attempts+2 == 11)
            endGame()
    }

    private fun endGame() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.end_game_dialog)
        dialog.message.text = getString(R.string.secretWordDisplay,word)
        dialog.setCancelable(false)
        dialog.againButton.setOnClickListener {
            dialog.dismiss()
            startGame()
        }
        dialog.show()
    }
}
