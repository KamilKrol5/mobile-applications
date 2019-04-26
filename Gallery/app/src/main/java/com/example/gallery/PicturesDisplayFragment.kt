package com.example.gallery

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.File

class PicturesDisplayFragment : Fragment() {

    val pictures = mutableListOf<Picture>()
    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/Camera"
    val directory = File(path)


    init {
        updatePictures()
    }

    fun updatePictures() {
        pictures.clear()
        val listAllFiles = directory.listFiles().sortedBy { -it.lastModified() }
        for (file in listAllFiles) {
            if (file.name.endsWith(".jpg")) {
                Log.i("FILE", file.absolutePath)
                pictures.add(Picture(file.absolutePath, 0, ""))
            }
        }
        if (::adapter.isInitialized)
            adapter.notifyDataSetChanged()
    }
//    public val pictures = mutableListOf<Picture>(
//        // FOR DEVELOPING PURPOSES
//        Picture(path + "/20190413_144108.jpg", 4, "xD"),
//        Picture(path + "/20190413_144108.jpg", 3, "xDD"),
//        Picture(path + "/20190413_144108.jpg", 2, "xDDD"),
//        Picture(path + "/20190413_144108.jpg", 1, "xDDDD"),
//        Picture(path + "/20190413_144108.jpg", 5, "xDDDDD"),
//        Picture(path + "/20190413_144108.jpg", 4, "xDDDDDD")
//    )


    lateinit var adapter: MyPictureRecyclerViewAdapter
    private var columnCount = 2

    var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_picture_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                val adpr = MyPictureRecyclerViewAdapter(pictures, listener)
                adapter = adpr
                this@PicturesDisplayFragment.adapter = adpr

                columnCount = resources.getInteger(R.integer.columns)
                layoutManager = GridLayoutManager(context, columnCount)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    //    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     *
//     *
//     * See the Android Training lesson
//     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
//     * for more information.
//     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(picture: Picture)

    }


    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        @JvmStatic
        fun newInstance(columnCount: Int) =
            PicturesDisplayFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
