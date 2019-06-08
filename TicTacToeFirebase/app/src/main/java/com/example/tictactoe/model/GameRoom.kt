package com.example.tictactoe.model


data class GameRoom(
    var player1Id: String = "null",
    var player1Name: String = "null",
    var player2Id: String = "null",
    var player2Name: String = "null",
    var currentUser: String = "null",
    var state: String = "---------",
    var accepted: Boolean = false,
    var started: Boolean = false,
    var interrupted: Boolean = false
) {
}