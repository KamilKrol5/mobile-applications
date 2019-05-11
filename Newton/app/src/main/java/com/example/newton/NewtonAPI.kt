package com.example.newton

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NewtonAPI {
    @GET("/{expression}/{operation}")
    public fun askNewtonAPI(@Path("operation") operation: String, @Path("expression") expression: String)
    : Call<NewtonAPIValue>
}

interface NewtonAPIArray {
    @GET("/{expression}/{operation}")
    public fun askNewtonAPI(@Path("operation") operation: String, @Path("expression") expression: String)
            : Call<NewtonAPIArrayValue>
}

data class NewtonAPIValue(var operation: String,
                          var expression: String,
                          var result: String)

data class NewtonAPIArrayValue(var operation: String,
                          var expression: String,
                          var result: List<String>)