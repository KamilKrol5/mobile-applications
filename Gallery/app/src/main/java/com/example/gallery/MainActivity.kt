package com.example.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*

const val PHOTO_VIEW_REQUEST_CODE = 124
const val CAMERA_REQUEST_CODE = 100

class MainActivity : AppCompatActivity() , PicturesDisplayFragment.OnListFragmentInteractionListener{

    var chosenIndex = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath +
                    "/Camera/$timeStamp.jpg"
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                    takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent

                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val file = File(path)

                    file.also {
                        val photoURI: Uri = GenericFileProvider.getUriForFile(
                            this,
                            "com.example.gallery.GenericFileProvider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                    }
                }
            }
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1)
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    1)
            }
        }

        val fragment = PicturesDisplayFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout,fragment,"display")
        transaction.commit()

        chosenIndex = savedInstanceState?.getInt("chosenIndex") ?: chosenIndex
    }


    override fun onListFragmentInteraction(picture: Picture) {
        //Toast.makeText(this,"Picked picture: "+picture.path,Toast.LENGTH_SHORT).show()
        val intent = Intent(this,PhotoViewActivity::class.java)
        intent.putExtra("chosenPicture", picture)
        val pics = (supportFragmentManager.findFragmentByTag("display") as PicturesDisplayFragment).pictures
//        intent.putExtra("index", pics.indexOf(picture))
        chosenIndex = pics.indexOf(picture)
        startActivityForResult(intent, PHOTO_VIEW_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_VIEW_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val newRating = data?.getIntExtra("newRating", -1)
                val newDescription = data?.getStringExtra("newDescription")
                val fragm = supportFragmentManager.findFragmentByTag("display") as PicturesDisplayFragment
                val pics = fragm.pictures
                pics[chosenIndex].rating = newRating ?: 0
                pics[chosenIndex].description = newDescription ?: "ERROR"
                fragm.adapter.notifyItemChanged(chosenIndex)
                pics.sortBy { -it.rating }
                fragm.adapter.notifyDataSetChanged()
            }
//            else if (resultCode == Activity.RESULT_CANCELED) {
//                val fragment = supportFragmentManager.findFragmentByTag("display")
//                val transaction = supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.frameLayout,fragment!!,"display")
//                transaction.commit()
//
//            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                Log.i("xdd","Camera result OK")
//                Log.i("xdd",data?.getStringExtra("output"))
//            }
            val fragm = supportFragmentManager.findFragmentByTag("display") as PicturesDisplayFragment
            fragm.updatePictures()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putInt("chosenIndex",chosenIndex)
    }

    public fun isPermissionGranted(permission:String):Boolean =
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED

//    fun onCLick(view: View) {
//        Toast.makeText(this, "Dziala!", Toast.LENGTH_SHORT).show()
//        val myintent = Intent(this, Main2Activity::class.java )
//        myintent.putExtra("data", editText.text.toString())
//        //startActivity(myintent)
//        startActivityForResult(myintent, 123)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 123) {
//            val s = data?.getStringExtra("wynik")
//            Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
//        }
//
//    }


//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}
