package com.example.tictactoe


import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.tictactoe.model.ActiveUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_user.view.*


class UserRecyclerViewAdapter(
    private val databaseRef: DatabaseReference,
    private val mListener: UsersFragment.OnListFragmentInteractionListener?
) : RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder>() {
    private val mSnapshots = AsyncListDiffer<DataSnapshot>(this, object : DiffUtil.ItemCallback<DataSnapshot>() {
        override fun areItemsTheSame(p0: DataSnapshot, p1: DataSnapshot): Boolean {
            return p0 == p1
        }

        override fun areContentsTheSame(p0: DataSnapshot, p1: DataSnapshot): Boolean {
            return p0.getValue(ActiveUser::class.java) == p1.getValue(ActiveUser::class.java)
        }
    })


    override fun getItemCount(): Int {
        return mSnapshots.currentList.size
    }

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ActiveUser
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
        val valListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.i("info", error.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
//                mSnapshots.clear()
//                snapshot.children.filter { it.child("inGame").value == false }.forEach {
//                    mSnapshots.add(it.key!!)
//                }
                mSnapshots.submitList(snapshot.children.toList())
            }

        }

        databaseRef.addValueEventListener(valListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val snapShot = mSnapshots.currentList[position]
        val user = snapShot.getValue(ActiveUser::class.java)
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
