package com.example.todo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_adding_task.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class AddingTask : Fragment() {
    private val cal = Calendar.getInstance()
    private var datetime = LocalDateTime.of(
        cal[Calendar.YEAR],
        cal[Calendar.MONTH],
        cal[Calendar.DAY_OF_MONTH],
        cal[Calendar.HOUR],
        cal[Calendar.MINUTE]
    )
    private var priority = 0
    private var pickedIcon = 0
    private var pickedColor = 0
//    private lateinit var previousBackgroundIcon: Drawable
    private var icons = mutableMapOf<Int, ImageView>()
    private var iconsRecourdes = listOf<Int>(
        R.drawable.ic_home_black_24dp,
        R.drawable.ic_fitness_center_black_24dp,
        R.drawable.ic_local_grocery_store_black_24dp,
        R.drawable.ic_work_black_24dp,
        R.drawable.ic_restaurant_black_24dp,
        R.drawable.ic_call_black_24dp,
        R.drawable.ic_school_black_24dp,
        R.drawable.ic_favorite_black_24dp,
        R.drawable.ic_pets_black_24dp,
        R.drawable.ic_local_florist_black_24dp
    )
    private var colors = mutableListOf<ImageView>()
    private var colorResources =
        mutableListOf(R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5)

    lateinit var addingTaskHandler: (Task) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adding_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState?.let {
            //          iconsRecourdes[pickedIcon] =  it.getInt("iconRes")
//          colorResources[pickedColor] = it.getInt("colorRes")
            pickedIcon = it.getInt("icon")
            pickedColor = it.getInt("color")
            datetime = it.getSerializable("date") as LocalDateTime
        }

        seekBar.max = 10
        seekBar.setOnSeekBarChangeListener(SeekbarListener())
        timeText.text = datetime.format(DateTimeFormatter.ofPattern("HH:mm"))
        dateText.text = datetime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"))
        timeText.setOnClickListener { showTimePickerDialog() }
        clockIcon.setOnClickListener { showTimePickerDialog() }
        dateText.setOnClickListener { showDatePickerDialog() }
        calendarIcon.setOnClickListener { showDatePickerDialog() }

        for (j in 0 until radioGroup1.childCount) {
            radioGroup1.getChildAt(j).setOnClickListener {
                pickIcon(j)
            }
            icons[j] = radioGroup1.getChildAt(j) as ImageView
        }
        for (j in 0 until radioGroup2.childCount) {
            radioGroup2.getChildAt(j).setOnClickListener {
                pickIcon(j + radioGroup1.childCount)
            }
            icons[j + radioGroup1.childCount] = radioGroup2.getChildAt(j) as ImageView
        }
//        previousBackgroundIcon = resources.getDrawable(R.color.transparent, null)

        for (i in 0 until colorsGroup.childCount) {
            colorsGroup.getChildAt(i).setOnClickListener {
                pickColor(i)
            }
            colors.add(i, colorsGroup.getChildAt(i) as ImageView)
        }
        addTaskButton.setOnClickListener { addTask() }

        pickColor(pickedColor)
        pickIcon(pickedIcon)
    }

    private fun pickColor(index: Int) {
        colors[pickedColor].background = null
        pickedColor = index
        val newColor = colorResources[pickedColor]
        editText.background = resources.getDrawable(newColor, null)
        calendarIcon.background = resources.getDrawable(newColor, null)
        clockIcon.background = resources.getDrawable(newColor, null)
        dateText.background = resources.getDrawable(newColor, null)
        timeText.background = resources.getDrawable(newColor, null)
        colors[pickedColor].background = resources.getDrawable(R.drawable.icon_frame, null)
    }

    private fun pickIcon(index: Int) {
//        icons.getValue(pickedIcon).background = previousBackgroundIcon
        icons.getValue(pickedIcon).background = null
        pickedIcon = index
        //previousBackgroundIcon = icons[pickedIcon]!!.background
        icons.getValue(pickedIcon).background = resources.getDrawable(R.drawable.icon_frame, null)
    }

    private fun showDatePickerDialog() {
        val picker = DatePickerDialog(
            context, { _, year, month, dayOfMonth -> setDate(year, month + 1, dayOfMonth) },
            datetime.year, datetime.monthValue, datetime.dayOfMonth
        )
        picker.show()
    }

    private fun showTimePickerDialog() {
        val picker = TimePickerDialog(
            context,
            { _, hourOfDay, minute -> setTime(hourOfDay, minute) },
            datetime.hour,
            datetime.minute,
            true
        )
        picker.show()
//        val fragmentManager = activity!!.supportFragmentManager
//        val fragment = MyTimePicker()
//        fragment.setTargetFragment(this,123)
//        fragment.show(fragmentManager,"TimePicker")

    }

    private fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        datetime = datetime.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth)
        val now = LocalDateTime.now()
        if (LocalDateTime.of(year, month, dayOfMonth, now.hour, now.minute).isBefore(now)) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.error)
            builder.setMessage(R.string.beforeCurrentDateError)
            val alert = builder.create()
        } else {
            dateText.text = datetime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"))
        }
    }

    private fun setTime(hourOfDay: Int, minute: Int) {
        datetime = datetime.withHour(hourOfDay).withMinute(minute)
        timeText.text = datetime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

//    fun onActivityResult() {
//        super.onActivityResult(requestCode, resultCode, data)
//        data!!.let {
//            datetime = LocalDateTime.of(it.getIntExtra("year",2300),
//                it.getIntExtra("month",1),
//                it.getIntExtra("day",1),
//                it.getIntExtra("hour",0),
//                it.getIntExtra("minute",0)
//                )
//            timeText.text = datetime.toString().replace('T',' ')
//        }
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        outState.putInt("iconRes",iconsRecourdes[pickedIcon])
//        outState.putInt("colorRes",colorResources[pickedColor])
        outState.putInt("icon", pickedIcon)
        outState.putInt("color", pickedColor)
        //outState.putInt("previousBackgroundIcon", previousBackgroundIcon)
        outState.putSerializable("date", datetime)
//        val fragm = fragmentManager?.findFragmentByTag("listFragment") as ListFragment
//        fragm.onSaveInstanceState(outState)
    }

    public fun addTask() {
        val task = Task(priority, editText.text.toString(), iconsRecourdes[pickedIcon], datetime, colorResources[pickedColor])
        addingTaskHandler.invoke(task)
        (activity as MainActivity).showingAddingTask = false
        fragmentManager!!.popBackStack()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddingTask()
    }

    inner class SeekbarListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            priority = seekBar!!.progress
            priorityTextView.text = priority.toString()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }

    }
}
