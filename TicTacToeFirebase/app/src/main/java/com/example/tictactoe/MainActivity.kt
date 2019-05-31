package com.example.tictactoe

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.tictactoe.model.ActiveUser
import com.example.tictactoe.model.GameRoom
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

const val AUTH_RES = 900

class MainActivity : AppCompatActivity(), GameFragment.OnFragmentInteractionListener,
    UsersFragment.OnListFragmentInteractionListener {
    override fun onFragmentInteraction() {

    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user: FirebaseUser? get() = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        //AuthUI.getInstance().signOut(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initApp()
    }

    override fun onStop() {
        super.onStop()
        AsyncTask.execute {
            //            val q = FirebaseFirestore.getInstance().collection("activeUsers").whereEqualTo("id", user?.uid).get()
//                .addOnCompleteListener {
//                    it.result?.apply {
//                        for (doc in this.documents) {
//                            doc.reference.delete()
//                        }
//                    }
//                }
            val id = auth.currentUser?.let {
                val g = FirebaseDatabase.getInstance().reference.child("activeUsers/${it.uid}").removeValue()
            }
        }
    }

    private fun initApp() {
        if (user == null) {
            login()
        } else {
            if (supportFragmentManager.findFragmentByTag("game") == null) {
                supportFragmentManager.beginTransaction().replace(R.id.mainLayout, ChooseModeFragment(), "chooseGame")
                    .commit()
            }
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

    override fun onListFragmentInteraction(user: ActiveUser) {
        if (user.id != auth.currentUser!!.uid) {
//            FirebaseFirestore.getInstance().collection("rooms").add(
//                GameRoom(
//                    auth.currentUser!!.uid,
//                    auth.currentUser!!.displayName.let { it } ?: "no name",
//                    user.id,
//                    user.name,
//                    currentUser = auth.currentUser!!.uid))
            FirebaseDatabase.getInstance().reference.child("rooms/${auth.currentUser!!.uid}-room").setValue(GameRoom(
                auth.currentUser!!.uid,
                auth.currentUser!!.displayName.let { it } ?: "no name",
                user.id,
                user.name,
                currentUser = auth.currentUser!!.uid))
        }
    }

}
