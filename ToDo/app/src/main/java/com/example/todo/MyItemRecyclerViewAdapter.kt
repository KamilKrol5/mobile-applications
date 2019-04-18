package com.example.todo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast


import kotlinx.android.synthetic.main.fragment_item.view.*
import java.time.format.DateTimeFormatter


class MyItemRecyclerViewAdapter(
    private val tasks: MutableList<Task>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val mOnLongClickListener: View.OnLongClickListener
    public var onItemRemovedHandler : ()->Unit = {}

    init {
        mOnClickListener = View.OnClickListener { v ->

            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.

        }
        mOnLongClickListener = View.OnLongClickListener { v ->
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tasks[position]

        with(holder.fragmentItem) {
            tag = item
            setOnClickListener(mOnClickListener)
            setOnLongClickListener(mOnLongClickListener)
            priorityField.text = item.priority.toString()
            deadlineText.text = item.deadline.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm"))
            taskContent.text = item.text
            icon.setImageResource(item.image)
            taskLayout.background = resources.getDrawable(item.color, null)
        }

        holder.itemView.setOnLongClickListener { v ->
            tasks.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,1)
            onItemRemovedHandler.invoke()
            true
        }
    }



    override fun getItemCount(): Int = tasks.size

    inner class ViewHolder(val fragmentItem: View) : RecyclerView.ViewHolder(fragmentItem) {

        override fun toString(): String {
            return super.toString()
        }

    }
}
