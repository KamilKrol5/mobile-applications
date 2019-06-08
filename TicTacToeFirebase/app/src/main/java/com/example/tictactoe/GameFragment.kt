package com.example.tictactoe

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableRow
import com.example.tictactoe.model.GameRoom
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_game.view.*
import kotlin.random.Random


const val dimension = 3
const val buttonMargin = 10
var buttonSize = 250

enum class Player {
    Cross, Circle
}

class GameFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var player2 = Player.Circle
    private var player1 = Player.Cross
    private var pictures = mapOf(
        player1 to R.drawable.cross,
        player2 to R.drawable.circle
    )
    var coordsToButtons = mutableMapOf<Pair<Int, Int>, ImageButton>()
    val game = TicTacToeGame(
        mutableMapOf<Pair<Int, Int>, Player>(),
        coordsToButtons,
        pictures.keys.elementAt((Random.nextInt(Player.values().size))),
        player1, player2, true
    )
    var online = false
    var currentUserId: String? = null
    var room: GameRoom? = null
    var roomDbRef : DatabaseReference? = null
    var onlineGameIdInRooms: String? = null
    var showSwitchPlayWithComputer = true

    private fun swapPlayers() {
        val tmp = player1
        player1 = player2
        player2 = tmp
        game.player1 = player1
        game.player2 = player2
    }

    private fun endGame(winner: Player?) {
        view!!.apply {
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
            game.areTilesResponsive = false
        }
    }

    fun makeMove(button: ImageButton, player: Player?) {
        if (player == null) return
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
        if (online) {
//            game.areTilesResponsive = !game.areTilesResponsive
            currentUserId = if (currentUserId == room!!.player1Id) room!!.player2Id else room!!.player1Id
            var rState = room!!.state
//            val pl1 = if (player1 == Player.Circle) Player.Circle else Player.Cross
//            val pl2 = if (pl1 == Player.Cross) Player.Circle else Player.Cross
            for (i in 1..dimension) {
                for (j in 1..dimension) {
                    rState = StringBuilder(rState).also {
                        it[3 * (i - 1) + j - 1] =
                            if (game.tiles[i to j] == player1) 'o' else if (game.tiles[i to j] == player2) 'x' else return@also
                    }.toString()
                }
            }
            val currUserID = currentUserId!!
            FirebaseDatabase.getInstance().reference.child("rooms").child(onlineGameIdInRooms!!)
                .setValue(room.apply { this!!.currentUser = currUserID; this.state = rState })
        }
    }

    fun performMove(button: ImageButton, player: Player?) {
        if (player == null) return
        when (player) {
            Player.Cross -> {
                button.setImageResource(pictures.getValue(player))
                changeCurrentPlayerImage(pictures.getValue(Player.Circle))
            }
            Player.Circle -> {
                button.setImageResource(pictures.getValue(player))
                changeCurrentPlayerImage(pictures.getValue(Player.Cross))
            }
        }
    }

    private fun changeCurrentPlayerImage(image: Int) {
        view!!.currentPlayerImageView!!.setImageResource(image)
    }

    fun restartAction() {
        if (view != null) startGame()
    }

    private fun startGame() {
        view!!.apply {
            game.tiles.clear()
            for (i in 0 until this.tableLayout.childCount) {
                val row = this.tableLayout.getChildAt(i)
                if (row is TableRow) {
                    for (j in 0 until row.childCount) {
                        (row.getChildAt(j) as ImageButton).setImageResource(0)
                    }
                }
            }
            this.turnLabel.visibility = View.VISIBLE
            this.currentPlayerImageView.visibility = View.VISIBLE
            this.winnerTextView.visibility = View.INVISIBLE
            this.winnerImageView.visibility = View.INVISIBLE
            game.currentPlayer = if (game.currentPlayer == player2) player1 else player2
            changeCurrentPlayerImage(pictures.getValue(game.currentPlayer))
            game.areTilesResponsive = true
            game.resetAIData()
            game.makeAIMove()
            if (online) {
                FirebaseDatabase.getInstance().reference.child("rooms").child(onlineGameIdInRooms!!)
                    .setValue(room.apply { this!!.state = "---------" })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.button21.setOnClickListener { startGame() }
        for (i in 1..dimension) {
            val tableRow = TableRow(requireContext()).apply {
                gravity = Gravity.CENTER
            }
            if (!showSwitchPlayWithComputer) {
                switchPlayWithComputer.visibility = View.INVISIBLE
            }
            for (j in 1..dimension) {
                tableRow.addView(ImageButton(requireContext()).apply {
                    val layoutParams2 = TableRow.LayoutParams(buttonSize, buttonSize)
                    layoutParams2.setMargins(buttonMargin, buttonMargin, buttonMargin, buttonMargin)
                    this.layoutParams = layoutParams2
                    setBackgroundResource(R.color.buttonColor)
                    setPadding(0, 0, 0, 0)
                    this.scaleType = ImageView.ScaleType.FIT_CENTER
                    this.setOnClickListener {
                        if (game.areTilesResponsive && !game.tiles.containsKey(i to j)) {
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
            view.tableLayout.addView(tableRow)
            view.switchPlayWithComputer.setOnClickListener {
                game.againstComputer = view.switchPlayWithComputer.isChecked
                game.makeAIMove()
            }

        }
        changeCurrentPlayerImage(pictures.getValue(game.currentPlayer))
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction()
    }

    override fun onPause() {
        super.onPause()
        room?.let {
            it.interrupted = true
            roomDbRef?.setValue(room)
            roomDbRef!!.removeValue()
            room = null
            roomDbRef = null
        }
    }
}
