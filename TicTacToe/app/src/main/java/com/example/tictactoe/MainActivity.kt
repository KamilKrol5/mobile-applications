package com.example.tictactoe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableRow
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

const val dimension = 5
const val buttonMargin = 10
var buttonSize = 250

enum class Player{
    Cross, Circle
}

class MainActivity : AppCompatActivity() {

    private var againstComputer = false
    private var areTilesResponsive = true
    private var player2 = Player.Circle
    private var player1 = Player.Cross
    private var pictures = mapOf(
        player1 to R.drawable.cross,
        player2 to R.drawable.circle
    )
    private var currentPlayer = pictures.keys.elementAt((Random.nextInt(Player.values().size)))
    private var tiles = mutableMapOf<Pair<Int,Int>,Player>()
    private var coordsToButtons = mutableMapOf<Pair<Int,Int>,ImageButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //buttonSize = (mainLayout.width / dimension)
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
                    this.scaleType = ImageView.ScaleType.FIT_CENTER
                    this.setOnClickListener {
                        if (areTilesResponsive && !tiles.containsKey(i to j)) {
                            tiles[i to j] = currentPlayer
                            makeMove(this, currentPlayer)
                            val winner = checkGameState()
                            if (winner != null) {
                                endGame(winner)
                            } else if (tiles.size == dimension * dimension) {
                                endGame(null)
                            } else {
                                makeAIMove()
                            }
                        }
                    }
                    coordsToButtons[i to j] = this
                })
            }
            tableLayout.addView(tableRow)
            switchPlayWithComputer.setOnClickListener {
                againstComputer = switchPlayWithComputer.isChecked
                makeAIMove()
            }

        }
        changeCurrentPlayerImage(pictures.getValue(currentPlayer))
    }

    private fun makeAIMove() {
        if (againstComputer && currentPlayer == player2 && areTilesResponsive) {
            val availableButtons = coordsToButtons.filterKeys { k -> !tiles.containsKey(k) }
            availableButtons.values.random().callOnClick()
        }
    }

    private fun endGame(winner: Player?) {
        //tableLayout.visibility = View.INVISIBLE
        turnLabel.visibility = View.INVISIBLE
        currentPlayerImageView.visibility = View.INVISIBLE
        winnerTextView.visibility = View.VISIBLE
        if (winner != null) {
            winnerTextView.text = "WINNER:"
            winnerImageView.setImageResource(pictures.getValue(winner))
            winnerImageView.visibility = View.VISIBLE
        } else {
            winnerImageView.visibility = View.INVISIBLE
            winnerTextView.text = "TIE!"
        }
        areTilesResponsive = false
    }

    private fun checkGameState() : Player? {
        for (i in 1..dimension) {
            tiles[i to 1]?.also { owner ->
                if ((2..dimension).all { tiles[i to it] == owner }) {
                    return owner
                }
            }

            tiles[1 to i]?.also { owner ->
                if ((2..dimension).all { tiles[it to i] == owner }) {
                    return owner
                }
            }
        }
        tiles[1 to 1]?.also { owner ->
            if ((2..dimension).all { tiles[it to it] == owner }) {
                return owner
            }
        }
        tiles[1 to dimension]?.also { owner ->
            if ((2..dimension).all { tiles[it to (dimension - it + 1)] == owner }) {
                return owner
            }
        }
        return null
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
    }

    private fun changeCurrentPlayerImage(image: Int) {
        currentPlayerImageView.setImageResource(image)
    }

    fun startGame(view: View) {
        tiles.clear()
        for (i in 0 until tableLayout.childCount) {
            val row = tableLayout.getChildAt(i)
            if (row is TableRow) {
                for (j in 0 until row.childCount) {
                    (row.getChildAt(j) as ImageButton).setImageResource(0)
                }
            }
        }
        turnLabel.visibility = View.VISIBLE
        currentPlayerImageView.visibility = View.VISIBLE
        winnerTextView.visibility = View.INVISIBLE
        winnerImageView.visibility = View.INVISIBLE
        currentPlayer = if (currentPlayer == player2) player1 else player2
        changeCurrentPlayerImage(pictures.getValue(currentPlayer))
        areTilesResponsive = true
        makeAIMove()
    }

}
