package com.example.tictactoe


import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.tictactoe.model.ActiveUser
import com.example.tictactoe.model.GameRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.choose_mode_framgent.view.*
import kotlinx.android.synthetic.main.fragment_user.view.*


class ChooseModeFragment : Fragment() {

    val auth = FirebaseAuth.getInstance()
    val dbRef = FirebaseDatabase.getInstance().reference
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
//        FirebaseFirestore.getInstance().collection("activeUsers")
//            .add(ActiveUser(auth.currentUser!!.displayName!!, false, auth.currentUser!!.uid))
        FirebaseDatabase.getInstance().reference.child("activeUsers").child(auth.currentUser!!.uid)
            .setValue(ActiveUser(auth.currentUser!!.displayName!!, false, auth.currentUser!!.uid))
            .addOnCanceledListener { Log.i("info", "adding user cancelled") }

        val usersFragment = UsersFragment()
        requireFragmentManager().beginTransaction().replace(R.id.mainLayout, usersFragment, "users")
            .addToBackStack("chooseUser")
            .commit()

        val gameFragment = GameFragment()
        dbRef.child("rooms").child(auth.currentUser!!.uid + "-room").addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.i("info", p0.toString())
                }

                override fun onDataChange(snapshot: DataSnapshot) {}
            }
        )
        dbRef.child("rooms").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.i("info", error.toString())
                throw error.toException()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                val gameRoom =
                    dataSnapshot.getValue(GameRoom::class.java) //?: throw RuntimeException("received gameRoom val is null")
                gameRoom?.let {
                    if (it.player2Id == auth.currentUser!!.uid || it.player1Id == auth.currentUser!!.uid) {
                        if (!gameRoom.interrupted && gameRoom.state == "---------" && !gameFragment.game.tiles.isEmpty()) {
                            gameFragment.roomDbRef = dataSnapshot.ref
                            gameFragment.restartAction()
                        }
                        if (it.accepted && !it.started && !gameRoom.interrupted) {
                            gameFragment.apply {
                                showSwitchPlayWithComputer = false
                                currentUserId = it.player1Id
                                room = it
                                game.areTilesResponsive = currentUserId == auth.currentUser!!.uid
                                roomDbRef = dataSnapshot.ref
//                                if (it.player1Id == auth.currentUser!!.uid) {
//                                    gameFragment.swapPlayers()
//                                }
                            }
                            gameFragment.online = true
                            gameFragment.onlineGameIdInRooms = dataSnapshot.key
                            dbRef.child("rooms").child(dataSnapshot.key!!)
                            requireFragmentManager().beginTransaction()
                                .replace(R.id.mainLayout, gameFragment, "game")
                                .addToBackStack("chooseGame")
                                .commit()
                            dbRef.child("rooms").child(dataSnapshot.key!!).setValue(it.apply { started = true })
                        } else if (it.started && !gameRoom.interrupted) {
                            gameFragment.room = it
                            gameFragment.roomDbRef = dataSnapshot.ref
                            gameFragment.currentUserId = it.currentUser
                            gameFragment.onlineGameIdInRooms = dataSnapshot.key
                            gameFragment.game.areTilesResponsive = gameFragment.currentUserId == auth.currentUser!!.uid
                            if (it.currentUser == auth.currentUser!!.uid) {
                                gameFragment.online = false
                                for (e in gameFragment.coordsToButtons.entries) {
                                    val charr = it.state.get(3 * (e.key.first - 1) + e.key.second - 1)
                                    when (charr) {
                                        '-' -> Unit
                                        'o' -> {

                                            e.value.callOnClick()

//                                            gameFragment.game.areTilesResponsive =
//                                                !gameFragment.game.areTilesResponsive
                                        }
                                        'x' -> {

                                            e.value.callOnClick()

//                                            gameFragment.game.areTilesResponsive =
//                                                !gameFragment.game.areTilesResponsive
                                        }
                                    }
                                }
                                gameFragment.online = true
                            }
                        } else if (gameRoom.interrupted) {
                            gameFragment.room = null
                            gameFragment.roomDbRef = null
                        }
                    }
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, pos: String?) {
                val gameRoom = dataSnapshot.getValue(GameRoom::class.java)
                if (gameRoom!!.player2Id == auth.currentUser!!.uid) {
                    AlertDialog.Builder(requireContext())
                        .setMessage("User ${gameRoom.player1Name} want to play with you. Do you accept it?")
                        .setPositiveButton("YES") { dialog, _ ->
                            dbRef.child("rooms").child(dataSnapshot.key!!).setValue(gameRoom.apply { accepted = true })
                            dialog.cancel()
                        }.setNegativeButton("NO") { dialog, _ ->
                            dbRef.child("rooms").child(dataSnapshot.key!!).removeValue()
                            dialog.cancel()
                        }.setCancelable(false).show()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val gameRoom =
                    snapshot.getValue(GameRoom::class.java) //?: throw RuntimeException("received gameRoom val is null")
                Log.i("info", "$gameRoom")
                gameRoom?.let {
                    if (it.player2Id == auth.currentUser!!.uid || it.player1Id == auth.currentUser!!.uid) {
                        if (!gameRoom.accepted) {
                            if (gameRoom.player1Id == auth.currentUser!!.uid && !gameRoom.interrupted) {
                                AlertDialog.Builder(requireContext()).setMessage("User rejected your game offer.")
                                    .setPositiveButton(
                                        "OK"
                                    ) { dialog, _ ->
                                        dialog.cancel()
                                    }.setCancelable(false).show()
                            }
                        } else if (gameRoom.interrupted) {
                            AlertDialog.Builder(requireContext()).setMessage("User left game.")
                                .setPositiveButton(
                                    "OK"
                                ) { dialog, _ ->
                                    dialog.cancel()
                                    requireFragmentManager().popBackStack(
                                        "chooseUser",
                                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                                    )
                                    val db = FirebaseDatabase.getInstance().reference
                                    val g = db.child("activeUsers/${auth.currentUser!!.uid}").removeValue()
                                    dbRef.child("rooms").removeEventListener(this)
                                }.setCancelable(false).show()
                        }
                    }
                }
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
