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
                val raw = response.raw()
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
                    editText.setText(res.toString())
                } else {
                    editText.setText("An error occurred")
                }
            }

        })
    }

    public fun buttonAbsoluteValueClicked(view: View) {
        callAPI("abs", editText.text.toString())
    }

    public fun buttonAreaUnderCurveClicked(view: View) {
        callAPI("area", editText.text.toString())
    }

    public fun buttonCosineClicked(view: View) {
        callAPI("cos", editText.text.toString())
    }

    public fun buttonDeriveClicked(view: View) {
        callAPI("derive", editText.text.toString())
    }

    public fun buttonFactorClicked(view: View) {
        callAPI("factor", editText.text.toString())
    }

    public fun buttonFindZerosClicked(view: View) {
        callAPIForArrayResult("zeroes", editText.text.toString())
    }

    public fun buttonIntegrateClicked(view: View) {
        callAPI("integrate", editText.text.toString())
    }

    public fun buttonFindTangentClicked(view: View) {
        callAPI("tangent", editText.text.toString())
    }

    public fun buttonInverseTangentClicked(view: View) {
        callAPI("arctan", editText.text.toString())
    }

    public fun buttonInverseCosineClicked(view: View) {
        callAPI("arccos", editText.text.toString())
    }

    public fun buttonInverseSineClicked(view: View) {
        callAPI("arcsin", editText.text.toString())
    }

    public fun buttonSimplifyClicked(view: View) {
        callAPI("simplify", editText.text.toString())
    }
    public fun buttonSineClicked(view: View) {
        callAPI("sin", editText.text.toString())
    }

    public fun buttonTangentClicked(view: View) {
        callAPI("tan", editText.text.toString())
    }

    public fun buttonLogarithmClicked(view: View) {
        callAPI("log", editText.text.toString())
    }
}
