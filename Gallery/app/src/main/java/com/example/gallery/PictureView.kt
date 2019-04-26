package com.example.gallery

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_picture_view.*
import kotlinx.android.synthetic.main.fragment_picture_view.view.*
import java.io.File

class PictureView : Fragment() {

    lateinit var picture : Picture
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pic = savedInstanceState?.getSerializable("picture")
        if (pic != null) {
            picture = pic as Picture
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_picture_view, container, false)
        view.button.setOnClickListener { onOkButtonPressed() }
        view.ratingBar2.rating = picture.rating.toFloat()
        view.editText.setText(picture.description)
        Picasso.get().load(File(picture.path)).error(R.drawable.ic_clear_black_24dp).into(view.imageView2)
        savedInstanceState?.let {
            view.ratingBar2.rating = it.getFloat("rating",view.ratingBar2.rating)
            view.editText.setText(it.getCharSequence("description"))
        } ?: Log.i("XD",savedInstanceState.toString())
        return view
    }

    private fun onOkButtonPressed() {
        picture.rating = ratingBar2.rating.toInt()
        picture.description = editText.text.toString()
        listener?.onFragmentInteraction(picture)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(picture: Picture)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("picture",picture)
        outState.putFloat("rating",ratingBar2.rating)
        outState.putString("description",editText.text.toString())
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PictureView().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
