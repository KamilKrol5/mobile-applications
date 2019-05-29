package com.example.tictactoe


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.tictactoe.model.ActiveUser
import com.example.tictactoe.model.GameRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.choose_mode_framgent.view.*
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_user.view.*


class ChooseModeFragment : Fragment() {

    val auth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.choose_mode_framgent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.buttonOnline.setOnClickListener { onlineGameChosen() }
        view.buttonThisPhone.setOnClickListener { localGameChosen() }
    }

    private fun localGameChosen() {
        val gameFragment = GameFragment()
        requireFragmentManager().beginTransaction().replace(R.id.mainLayout, gameFragment, "game")
            .addToBackStack("chooseGame")
            .commit()
    }

    private fun onlineGameChosen() {
        FirebaseFirestore.getInstance().collection("activeUsers")
            .add(ActiveUser(auth.currentUser!!.displayName!!, false, auth.currentUser!!.uid))

        val usersFragment = UsersFragment()
        requireFragmentManager().beginTransaction().replace(R.id.mainLayout, usersFragment, "users")
            .addToBackStack("chooseGame")
            .commit()

        val gameFragment = GameFragment()
        FirebaseFirestore.getInstance().collection("rooms")
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshot, e ->
                if (e != null) {
                    Log.w("info", "Listen failed.", e)
                    return@EventListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    for (change in snapshot.documentChanges) {
                        val gameRoom = change.document.toObject(GameRoom::class.java)
                        if (change.type == DocumentChange.Type.ADDED) {
                            if (gameRoom.player2Id == auth.currentUser!!.uid) {
                                AlertDialog.Builder(requireContext())
                                    .setMessage("User ${gameRoom.player1Name} want to play with you. Do you accept it?")
                                    .setPositiveButton("YES", DialogInterface.OnClickListener { dialog, _ ->
                                        FirebaseFirestore.getInstance().collection("rooms").document(change.document.id)
                                            .update(mapOf("accepted" to true))
                                        dialog.cancel()
                                    }).setNegativeButton("NO", DialogInterface.OnClickListener { dialog, _ ->
                                        FirebaseFirestore.getInstance().collection("rooms").document(change.document.id)
                                            .delete()
                                        dialog.cancel()
                                    }).setCancelable(false).show()
                            }
                        } else if (change.type == DocumentChange.Type.MODIFIED) {
                            if (gameRoom.player2Id == auth.currentUser!!.uid || gameRoom.player1Id == auth.currentUser!!.uid) {
                                if (gameRoom.accepted && !gameRoom.started) {
                                    gameFragment.apply {
                                        showSwitchPlayWithComputer = false
                                        currentUserId = gameRoom.player1Id
                                        room = gameRoom
                                        game.areTilesResponsive = currentUserId == auth.currentUser!!.uid
                                        if (gameRoom.player1Id == auth.currentUser!!.uid) {
                                            gameFragment.swapPlayers()
                                        }
                                    }
                                    gameFragment.online = true
                                    gameFragment.onlineGameDocument =
                                        FirebaseFirestore.getInstance().collection("rooms").document(change.document.id)
                                    requireFragmentManager().beginTransaction()
                                        .replace(R.id.mainLayout, gameFragment, "game")
                                        .addToBackStack("chooseGame")
                                        .commit()
                                    FirebaseFirestore.getInstance().collection("rooms").document(change.document.id)
                                        .update(mapOf("started" to true))
                                } else if (gameRoom.started) {
                                    gameFragment.room = gameRoom
                                    gameFragment.onlineGameDocument =
                                        FirebaseFirestore.getInstance().collection("rooms").document(change.document.id)
                                    if (gameRoom.currentUser == auth.currentUser!!.uid) {
                                        for (e in gameFragment.coordsToButtons.entries) {
                                            val charr = gameRoom.state.get(3 * (e.key.first - 1) + e.key.second - 1)
                                            when (charr) {
                                                '-' -> Unit
                                                'o' -> {
                                                    gameFragment.makeMove(
                                                        e.value,
                                                        if (gameFragment.game.currentPlayer != gameFragment.game.player2) gameFragment.game.currentPlayer else null
                                                    )
                                                    gameFragment.game.areTilesResponsive =
                                                        !gameFragment.game.areTilesResponsive
                                                }
                                                'x' -> {
                                                    gameFragment.makeMove(
                                                        e.value,
                                                        if (gameFragment.game.currentPlayer != gameFragment.game.player1) gameFragment.game.currentPlayer else null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (change.type == DocumentChange.Type.REMOVED) {
                            Log.i("info", "$gameRoom")
                            if (gameRoom.player1Id == auth.currentUser!!.uid) {
                                AlertDialog.Builder(requireContext()).setMessage("User rejected your game offer.")
                                    .setPositiveButton(
                                        "OK"
                                    ) { dialog, _ ->
                                        dialog.cancel()
                                    }.setCancelable(false).show()
                            }
                        }
                    }

                    Log.d("info", "Current data: ${snapshot.documents}")
                } else {
                    Log.d("info", "Current data: null")
                }
            })
    }


}

class RoomViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
    val nameFieldView: TextView = mView.nameField
    val inGameFieldView: TextView = mView.inGameField

    override fun toString(): String {
        return super.toString() + " '" + nameFieldView.text + "'"
    }
}
