package com.example.todo

import android.graphics.drawable.Drawable
import java.io.Serializable
import java.time.LocalDateTime
import java.util.Date

data class Task(var priority: Int, var text: String, val image: Int, var deadline: LocalDateTime, val color: Int) : Serializable{

}