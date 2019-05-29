package com.example.tictactoe.model


data class GameRoom(
    val player1Id: String = "null",
    val player1Name: String = "null",
    val player2Id: String = "null",
    val player2Name: String = "null",
    val currentUser: String = "null",
    val state: String = "---------",
    var accepted: Boolean = false,
    var started: Boolean = false
) {
}