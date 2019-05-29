package com.example.tictactoe

import android.arch.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainViewModel : ViewModel() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? get() = auth.currentUser
}