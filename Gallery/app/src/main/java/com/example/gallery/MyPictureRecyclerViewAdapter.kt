package com.example.gallery

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


//import com.example.gallery.PicturesDisplayFragment.OnListFragmentInteractionListener
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.fragment_picture.view.*
import java.io.File

class MyPictureRecyclerViewAdapter(
    private val mValues: List<Picture>,
    private val mListener: PicturesDisplayFragment.OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyPictureRecyclerViewAdapter.ViewHolder>() {

    init {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_picture, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        with(holder.mView) {
            Log.i("XD",item.path)
            Log.i("XD",File(item.path).exists().toString())
            Picasso.get().load(File(item.path)).error(R.drawable.ic_camera_alt_black_24dp).into(imageView)
            ratingBar.rating = item.rating.rem(ratingBar.max).toFloat()
            setOnClickListener{
                mListener?.onListFragmentInteraction(item)
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {


        override fun toString(): String {
            return super.toString()
        }
    }

}
