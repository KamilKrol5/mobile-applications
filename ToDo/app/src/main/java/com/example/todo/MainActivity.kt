package com.example.todo

import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.JsonWriter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

//    var taskList = mutableListOf<Task>()
    lateinit var frgmt : ListFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            showAddingTaskFragment()
        }
        if (savedInstanceState == null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = ListFragment()
            frgmt = fragment
            fragmentTransaction.add(R.id.frameLayout, fragment, "listFragment")
            fragmentTransaction.commit()
        } else {
            frgmt = supportFragmentManager.findFragmentByTag("listFragment") as ListFragment
        }
    }

    private fun showAddingTaskFragment() {
        fab.hide()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = AddingTask()
        fragment.addingTaskHandler = { task ->
            frgmt.taskList.add(task)
            fab.show()
        }
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        fab.show()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    companion object {
        val sortings = listOf<Pair<String, (Task) -> Int>>(
            "color" to { task : Task -> task.color },
            "deadline" to { task : Task -> task.deadline.year+task.deadline.monthValue+task.deadline.hour+task.deadline.minute },
            "priority" to { task : Task -> task.priority },
            "image" to { task : Task -> task.image }
        )
        var cmpCounter = 0
    }

    public fun sortTasks(menuItem: MenuItem) {
        frgmt.taskList.sortByDescending { sortings[cmpCounter].second.invoke(it)}
        Log.i("xD",frgmt.taskList.toString())
        frgmt.recyclerView2.adapter?.notifyDataSetChanged()
        Snackbar.make(coordinatorLayout, "Sorting by ${sortings[cmpCounter].first}", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
        cmpCounter++
        cmpCounter = cmpCounter.rem(sortings.size - 1)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val gson = Gson()
        val str = gson.toJson(frgmt.taskList)
        val file = File(filesDir,"tasks.json")
        val writer = BufferedWriter(FileWriter(file,false))
        writer.append(str)
        writer.flush()
        writer.close()
    }
}
