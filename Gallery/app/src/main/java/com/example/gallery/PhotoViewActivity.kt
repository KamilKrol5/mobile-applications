package com.example.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class PhotoViewActivity : AppCompatActivity(), PictureView.OnFragmentInteractionListener{

    override fun onFragmentInteraction(picture: Picture) {
        val intent = Intent()
        intent.putExtra("newRating",picture.rating)
        intent.putExtra("newDescription", picture.description)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    lateinit var picture: Picture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view_activity)

        picture = intent.getSerializableExtra("chosenPicture") as Picture

        if (savedInstanceState == null) {
            val fragment = PictureView()
            fragment.picture = picture
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayoutView, fragment, "pictureView")
            transaction.commit()
        }
    }

}
