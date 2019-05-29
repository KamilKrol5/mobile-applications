package com.example.newton

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        retrofit = Retrofit.Builder()
            .baseUrl("https://newton.now.sh")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }


            private fun callAPI(expression: String, operation: String) {
                val newton = retrofit.create(NewtonAPI::class.java)
                val call = newton.askNewtonAPI(operation, expression)
                call.enqueue(object : Callback<NewtonAPIValue> {
                    override fun onFailure(call: Call<NewtonAPIValue>, t: Throwable) {
                        editText.setText("An error occurred")
                    }

                    override fun onResponse(call: Call<NewtonAPIValue>, response: Response<NewtonAPIValue>) {
                        val body = response.body()
                        if (body != null) {
                            val res = body.result
                            editText.setText(res)
                        } else {
                            editText.setText("An error occurred")
                        }
                    }
        })
    }

    private fun callAPIForArrayResult(expression: String, operation: String) {
        val newton = retrofit.create(NewtonAPIArray::class.java)
        val call = newton.askNewtonAPI(operation, expression)
        call.enqueue(object : Callback<NewtonAPIArrayValue> {
            override fun onFailure(call: Call<NewtonAPIArrayValue>, t: Throwable) {
                editText.setText("An error occurred")
            }

            override fun onResponse(call: Call<NewtonAPIArrayValue>, response: Response<NewtonAPIArrayValue>) {
                val body = response.body()
                if (body != null) {
                    val res = body.result
                    editText?.setText(res?.toString())
                } else {
                    editText.setText("An error occurred")
                }
            }

        })
    }

    fun buttonClicked(view: View) {
        callAPI(view.tag.toString(), editText.text.toString())
    }

    fun buttonFindZerosClicked(view: View) {
        callAPIForArrayResult("zeroes", editText.text.toString())
    }

}
