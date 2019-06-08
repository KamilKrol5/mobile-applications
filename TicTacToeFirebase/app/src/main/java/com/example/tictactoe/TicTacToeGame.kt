package com.example.tictactoe

import android.widget.ImageButton
import kotlin.math.ceil

class TicTacToeGame(
    val tiles: MutableMap<Pair<Int, Int>, Player>,
    private val coordsToButtons: MutableMap<Pair<Int, Int>, ImageButton>,
    var currentPlayer: Player,
    var player1: Player,
    var player2: Player,
    var areTilesResponsive: Boolean
) {
    var againstComputer = false

    private val limit = ceil(dimension / 2.0)
    private var diagonalL = 0
    private var diagonalR = 0
    private val aiMoveFunctions = listOf(
        ::moveHorizontal, ::moveVertical
    )

    fun updateAIData(first: Int, second: Int) {
        if (first == second) {
            diagonalL++
        } else if (first == dimension - second + 1) {
            diagonalR++
        }
    }

    fun resetAIData() {
        diagonalL = 0
        diagonalR = 0
    }

    fun makeAIMove() {
        if (againstComputer && currentPlayer == player2 && areTilesResponsive) {
            val availableButtons = coordsToButtons.filterKeys { k -> !tiles.containsKey(k) }
            val enemyButtons = tiles.filter { k -> k.value == player1 }
            if (enemyButtons.isNotEmpty()) {
                val coordinates = enemyButtons.keys.last()
                val randomIndex = (0 until aiMoveFunctions.size).random()
                if (diagonalL >= limit) {
                    for (i in 1..dimension)
                        for (j in 0 until dimension)
                            if (moveDiagonal(availableButtons, i to i, j, true, false)) return

                } else if (diagonalR >= limit) {
                    for (i in 1..dimension)
                        for (j in 0 until dimension)
                            if (moveDiagonal(availableButtons, i to (dimension - i + 1), j, false, true)) return
                }
                // if dangerous situation
                for (i in 1..dimension) {
                    var horizontal = 0
                    var vertical = 0
                    for (j in 1..dimension) {
                        if (enemyButtons.containsKey(i to j)) horizontal++
                        if (enemyButtons.containsKey(j to i)) vertical++
                    }
                    if (horizontal >= limit)
                        for (k in 0 until (dimension + 1))
                            if (moveHorizontal(availableButtons, i to 1, k)) return
                    if (vertical >= limit)
                        for (k in 0 until (dimension + 1))
                            if (moveVertical(availableButtons, 1 to i, k)) return
                }
                for (i in 0 until dimension) {
                    when {
                        aiMoveFunctions[randomIndex].invoke(availableButtons, coordinates, i) -> return
                        aiMoveFunctions[(randomIndex + 1).rem(aiMoveFunctions.size)].invoke(
                            availableButtons,
                            coordinates,
                            i
                        ) -> return
//                        aiMoveFunctions[(randomIndex+2).rem(aiMoveFunctions.size)].invoke(availableButtons,coordinates,i) -> return
                        moveDiagonal(availableButtons, coordinates, i, true, true) -> return
                    }
                }
                availableButtons.values.random().callOnClick()
            } else
                availableButtons.values.random().callOnClick()
        }
    }

    private fun moveDiagonal(
        availableButtons: Map<Pair<Int, Int>, ImageButton>,
        coordinates: Pair<Int, Int>, step: Int, checkLeft: Boolean, checkRight: Boolean
    ): Boolean {
        return if (checkLeft && coordinates.first == coordinates.second &&
            availableButtons.containsKey(
                (coordinates.first + step).rem(dimension) + 1 to (coordinates.second + step).rem(
                    dimension
                ) + 1
            )
        ) {
            val chosenCoordinates =
                (coordinates.first + step).rem(dimension) + 1 to (coordinates.second + step).rem(dimension) + 1
            availableButtons[chosenCoordinates]?.callOnClick()
            true
        } else if (checkRight && coordinates.first == dimension - coordinates.second + 1 &&
            availableButtons.containsKey(
                (coordinates.first + step).rem(dimension) + 1 to (coordinates.second - 1 - step + dimension - 1).rem(
                    dimension
                ) + 1
            )
        ) {
            val chosenCoordinates =
                (coordinates.first + step).rem(dimension) + 1 to (coordinates.second - 1 - step + dimension - 1).rem(
                    dimension
                ) + 1
            availableButtons[chosenCoordinates]?.callOnClick()
            true
        } else false
    }

    private fun moveVertical(
        availableButtons: Map<Pair<Int, Int>, ImageButton>,
        coordinates: Pair<Int, Int>,
        step: Int
    ): Boolean {
        return if (availableButtons.containsKey((coordinates.first + step).rem(dimension) + 1 to coordinates.second)) {
            val chosenCoordinates = (coordinates.first + step).rem(dimension) + 1 to coordinates.second
            availableButtons[chosenCoordinates]?.callOnClick()
            true
        } else false
    }

    private fun moveHorizontal(
        availableButtons: Map<Pair<Int, Int>, ImageButton>,
        coordinates: Pair<Int, Int>,
        step: Int
    ): Boolean {
        return if (availableButtons.containsKey(coordinates.first to (coordinates.second + step).rem(dimension) + 1)) {
            val chosenCoordinates = coordinates.first to (coordinates.second + step).rem(dimension) + 1
            availableButtons[chosenCoordinates]?.callOnClick()
            true
        } else false
    }

    fun checkGameState(): Player? {
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
}