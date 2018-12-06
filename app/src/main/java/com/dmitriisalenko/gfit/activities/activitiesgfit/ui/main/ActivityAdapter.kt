package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.dmitriisalenko.gfit.activities.activitiesgfit.R
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataActivity
import kotlinx.android.synthetic.main.activity_item.view.*
import java.text.DateFormat

class ActivityAdapter(
    private val ctx : Context,
    private val activities: ArrayList<DataActivity>
) : ArrayAdapter<DataActivity>(ctx, 0, activities) {

    override fun getView(position : Int, convertView : View?, parent : ViewGroup) : View {
        val dataActivity = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_item, parent, false)

        (view as ActivityItemView).dataActivity = dataActivity

        val timeFormat = DateFormat.getTimeInstance()

        view.activity_time.text = "${timeFormat.format(dataActivity.startTime)} - ${timeFormat.format(dataActivity.endTime)}"
        view.activity_name.text = dataActivity.activity

        return view
    }

}