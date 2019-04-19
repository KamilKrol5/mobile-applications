package com.example.gallery

import java.io.FileDescriptor
import java.io.Serializable

data class Picture(
    val path : String, var rating : Int, var description : String) : Serializable