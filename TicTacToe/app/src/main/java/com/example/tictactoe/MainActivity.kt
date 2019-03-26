package com.example.tictactoe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableRow
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.ceil
import kotlin.random.Random

const val dimension = 5
const val buttonMargin = 10
var buttonSize = 250

enum class Player{
    Cross, Circle
}

class MainActivity : AppCompatActivity() {
    private val limit = ceil(dimension/2.0)
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
    private val aiMoveFunctions = listOf(
        ::moveHorizontal,::moveVertical
    )
    private var diagonalL = 0
    private var diagonalR = 0
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
                            if (currentPlayer == player1) {
                                updateAIData(i,j)
                            }
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

    private fun updateAIData(first: Int, second: Int) {
        if (first == second) {
            diagonalL++
        } else if (first == dimension - second + 1) {
            diagonalR++
        }
    }

    private fun makeAIMove() {
        if (againstComputer && currentPlayer == player2 && areTilesResponsive) {
            val availableButtons = coordsToButtons.filterKeys { k -> !tiles.containsKey(k) }
            val enemyButtons = tiles.filter { k -> k.value == player1 }
            if (enemyButtons.isNotEmpty()) {
                val coordinates = enemyButtons.keys.last()
                var randomIndex = (0 until aiMoveFunctions.size).random()
                if(diagonalL >= limit) {
                    for (i in 1..dimension)
                        for (j in 0 until dimension)
                            if (moveDiagonal(availableButtons,i to i,j,true,false)) return

                } else if (diagonalR >= limit) {
                    for (i in 1..dimension)
                        for (j in 0 until dimension)
                            if (moveDiagonal(availableButtons, i to (dimension - i + 1), j,false,true)) return
                }
                // if dangerous situation
                for (i in 1..dimension) {
                    var horizontal = 0
                    var vertical = 0
                    for (j in 1..dimension) {
                        if (enemyButtons.containsKey(i to j)) horizontal ++
                        if (enemyButtons.containsKey(j to i)) vertical++
                    }
                    if (horizontal >= limit)
                        for (k in 0 until (dimension+1))
                            if (moveHorizontal(availableButtons, i to 1,k)) return
                    if (vertical >= limit)
                        for (k in 0 until (dimension+1))
                            if (moveVertical(availableButtons, 1 to i,k)) return
                }
                for (i in 0 until dimension) {
                    when {
                        aiMoveFunctions[randomIndex].invoke(availableButtons,coordinates,i) -> return
                        aiMoveFunctions[(randomIndex+1).rem(aiMoveFunctions.size)].invoke(availableButtons,coordinates,i) -> return
//                        aiMoveFunctions[(randomIndex+2).rem(aiMoveFunctions.size)].invoke(availableButtons,coordinates,i) -> return
                        moveDiagonal(availableButtons,coordinates,i,true,true) -> return
                    }
                }
                availableButtons.values.random().callOnClick()
            } else
                availableButtons.values.random().callOnClick()
        }
    }

    private fun moveDiagonal(availableButtons: Map<Pair<Int, Int>, ImageButton>,
                             coordinates: Pair<Int, Int>,step : Int, checkLeft : Boolean,checkRight : Boolean) : Boolean {
        return if (checkLeft && coordinates.first == coordinates.second &&
            availableButtons.containsKey((coordinates.first + step).rem(dimension)+1 to (coordinates.second + step).rem(dimension)+1)) {
                val chosenCoordinates = (coordinates.first + step).rem(dimension)+1 to (coordinates.second + step).rem(dimension)+1
                availableButtons[chosenCoordinates]?.callOnClick()
                true
            } else if (checkRight && coordinates.first == dimension - coordinates.second +1 &&
                availableButtons.containsKey((coordinates.first + step).rem(dimension)+1 to (coordinates.second-1 - step + dimension-1).rem(dimension)+1)) {
                val chosenCoordinates = (coordinates.first + step).rem(dimension)+1 to (coordinates.second-1 - step + dimension-1 ).rem(dimension)+1
                availableButtons[chosenCoordinates]?.callOnClick()
                true
        }
        else false
    }

    private fun moveVertical(availableButtons: Map<Pair<Int, Int>, ImageButton>, coordinates: Pair<Int, Int>,step : Int) : Boolean{
        return if (availableButtons.containsKey((coordinates.first + step).rem(dimension) + 1 to coordinates.second)) {
            val chosenCoordinates = (coordinates.first + step).rem(dimension) + 1 to coordinates.second
            availableButtons[chosenCoordinates]?.callOnClick()
            true
        } else false
    }

    private fun moveHorizontal(availableButtons: Map<Pair<Int, Int>, ImageButton>, coordinates: Pair<Int, Int>,step : Int) : Boolean{
        return if (availableButtons.containsKey(coordinates.first to (coordinates.second + step).rem(dimension)+1)) {
            val chosenCoordinates = coordinates.first to (coordinates.second + step).rem(dimension)+1
            availableButtons[chosenCoordinates]?.callOnClick()
            true } else false
    }


    private fun endGame(winner: Player?) {
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

    private fun resetAIData() {
        diagonalL = 0
        diagonalR = 0
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
        resetAIData()
        makeAIMove()
    }

}
