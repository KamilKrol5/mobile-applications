package com.example.todo

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tasks_view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader

class ListFragment : Fragment() {

    private var columnCount = 1
    var taskList = mutableListOf<Task>()
    lateinit var recyclerView2 : RecyclerView

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
//        savedInstanceState?.let {
//            val size = it.getInt("taskListSize",0)
//            val newTaskList = mutableListOf<Task>()
//            for (i in 0 until size) {
//                newTaskList.add(it.getSerializable("task$i") as Task)
//                taskList = newTaskList
//            }
//        }





        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                recyclerView2 = this
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemRecyclerViewAdapter(taskList)
                (adapter as MyItemRecyclerViewAdapter).apply {
                    onItemRemovedHandler = { Snackbar.make(view, "Task done!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show() }
                    notifyDataSetChanged()
                }
            }
        }
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("taskListSize",taskList.size)
        for (i in 0 until taskList.size)
            outState.putSerializable("task$i", taskList[i])

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
