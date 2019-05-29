package com.example.tictactoe

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.tictactoe.model.ActiveUser


import com.example.tictactoe.model.FirestoreAdapter
import com.google.firebase.firestore.Query

import kotlinx.android.synthetic.main.fragment_user.view.*


class UserRecyclerViewAdapter(
    private val query: Query,
    private val mListener: UsersFragment.OnListFragmentInteractionListener?
) : FirestoreAdapter<UserRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ActiveUser
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
        setQuery(query)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val snapShot = getSnapshot(position)
        val user = snapShot.toObject(ActiveUser::class.java)
        holder.nameFieldView.text = user!!.name
        holder.inGameFieldView.text = user.inGame.toString()

        with(holder.mView) {
            tag = user
            setOnClickListener(mOnClickListener)
        }
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val nameFieldView: TextView = mView.nameField
        val inGameFieldView: TextView = mView.inGameField

        override fun toString(): String {
            return super.toString() + " '" + nameFieldView.text + "'"
        }
    }


}
