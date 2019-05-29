package com.example.tictactoe

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableRow
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

const val AUTH_RES = 900
const val dimension = 3
const val buttonMargin = 10
var buttonSize = 250

enum class Player{
    Cross, Circle
}

class MainActivity : AppCompatActivity() {

    private var areTilesResponsive = true
    private var player2 = Player.Circle
    private var player1 = Player.Cross
    private var pictures = mapOf(
        player1 to R.drawable.cross,
        player2 to R.drawable.circle
    )
    private var coordsToButtons = mutableMapOf<Pair<Int,Int>,ImageButton>()
    private val game = TicTacToeGame(mutableMapOf<Pair<Int,Int>,Player>(),
        coordsToButtons,
        pictures.keys.elementAt((Random.nextInt(Player.values().size))),
        player1, player2, areTilesResponsive)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user: FirebaseUser? get() = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        //AuthUI.getInstance().signOut(this)
        super.onCreate(savedInstanceState)
        initApp()
    }

    private fun initApp() {
        if (user == null) {
            login()
        } else {

            setContentView(R.layout.activity_main)
            for (i in 1..dimension) {
                val tableRow = TableRow(this).apply {
                    gravity = Gravity.CENTER
                }
                for (j in 1..dimension) {
                    tableRow.addView(ImageButton(this).apply {
                        val layoutParams2 = TableRow.LayoutParams(buttonSize, buttonSize)
                        layoutParams2.setMargins(buttonMargin, buttonMargin, buttonMargin, buttonMargin)
                        this.layoutParams = layoutParams2
                        setBackgroundResource(R.color.buttonColor)
                        setPadding(0, 0, 0, 0)
                        this.scaleType = ImageView.ScaleType.FIT_CENTER
                        this.setOnClickListener {
                            if (areTilesResponsive && !game.tiles.containsKey(i to j)) {
                                game.tiles[i to j] = game.currentPlayer
                                if (game.currentPlayer == player1) {
                                    game.updateAIData(i, j)
                                }
                                makeMove(this, game.currentPlayer)
                                val winner = game.checkGameState()
                                if (winner != null) {
                                    endGame(winner)
                                } else if (game.tiles.size == dimension * dimension) {
                                    endGame(null)
                                } else {
                                    game.makeAIMove()
                                }
                            }
                        }
                        coordsToButtons[i to j] = this
                    })
                }
                tableLayout.addView(tableRow)
                switchPlayWithComputer.setOnClickListener {
                    game.againstComputer = switchPlayWithComputer.isChecked
                    game.makeAIMove()
                }

            }
            changeCurrentPlayerImage(pictures.getValue(game.currentPlayer))
        }
    }

    private fun login() {
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            AUTH_RES
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTH_RES) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                initApp()
            } else {
                if (response == null) {
                    finish()
                    return
                }

                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()
                    finish()
                    return
                }
                Toast.makeText(this, "Sign-in error: ${response.error?.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    }

    private fun endGame(winner: Player?) {
        turnLabel.visibility = View.INVISIBLE
        currentPlayerImageView.visibility = View.INVISIBLE
        winnerTextView.visibility = View.VISIBLE
        if (winner != null) {
            winnerTextView.text = getString(R.string.winner_label)
            winnerImageView.setImageResource(pictures.getValue(winner))
            winnerImageView.visibility = View.VISIBLE
        } else {
            winnerImageView.visibility = View.INVISIBLE
            winnerTextView.text = getString(R.string.tie_label)
        }
        areTilesResponsive = false
    }

    private fun makeMove(button: ImageButton,player: Player) {
        game.currentPlayer = when (player) {
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
        game.tiles.clear()
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
        game.currentPlayer = if (game.currentPlayer == player2) player1 else player2
        changeCurrentPlayerImage(pictures.getValue(game.currentPlayer))
        areTilesResponsive = true
        game.resetAIData()
        game.makeAIMove()
    }

}
