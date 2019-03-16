package com.example.tictactoe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableRow
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

const val dimension = 5
const val buttonMargin = 10
var buttonSize = 150

enum class Player{
    Cross, Circle
}

data class Tile(var visited : Boolean)

class MainActivity : AppCompatActivity() {

    private var player2 = Player.Circle
    private var player1 = Player.Cross
//    var player2Img = R.drawable.circle
//    var player1Img = R.drawable.cross
    var pictures = mapOf(
        player1 to R.drawable.circle,
        player2 to R.drawable.cross
    )
    private var currentPlayer = pictures.keys.elementAt((Random.nextInt(Player.values().size)))
    private var tiles = mutableMapOf<Pair<Int,Int>,Player>()

    override fun onCreate(savedInstanceState: Bundle?) {
    Log.i("XD","$currentPlayer")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //buttonSize = (mainLayout.width / dimension)
        Log.i("XD","$buttonSize")
        for (i in 1..dimension) {
            val tableRow = TableRow(this).apply {
                gravity = Gravity.CENTER
            }
            for (j in 1..dimension) {
                tableRow.addView(ImageButton(this).apply {
                    val layoutParams2 = TableRow.LayoutParams(buttonSize, buttonSize)
                    layoutParams2.setMargins(buttonMargin, buttonMargin,buttonMargin,buttonMargin)
                    this.layoutParams = layoutParams2
                    setBackgroundResource(R.color.buttonColor)
                    setPadding(0,0,0,0)
                    //this.setImageResource(R.drawable.circle)
                    this.scaleType = ImageView.ScaleType.FIT_CENTER
                    this.setOnClickListener {
                        makeMove(this, currentPlayer)
                        tiles[i to j] = currentPlayer
                        checkGameState()
                    }
                })
            }
            tableLayout.addView(tableRow)
        }
        changeCurrentPlayerImage(pictures.getValue(currentPlayer))
    }

    private fun checkGameState() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun makeMove(button: ImageButton,player: Player) {
        currentPlayer = when (player) {
            Player.Cross -> {
                button.setImageResource(pictures.getValue(player))
                changeCurrentPlayerImage(pictures.getValue(Player.Circle))
                player2
            }
            Player.Circle -> {
                button.setImageResource(pictures.getValue(player))
                changeCurrentPlayerImage(pictures.getValue(Player.Cross))
                player1
            }
        }
        button.setOnClickListener { }
    }

    private fun changeCurrentPlayerImage(image: Int) {
        currentPlayerImageView.setImageResource(image)
    }

}
