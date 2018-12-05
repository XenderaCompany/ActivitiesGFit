package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.dmitriisalenko.gfit.activities.activitiesgfit.R
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataActivity
import kotlinx.android.synthetic.main.activity_item.view.*

class ActivityAdapter(
    private val ctx : Context,
    private val activities: ArrayList<DataActivity>
) : ArrayAdapter<DataActivity>(ctx, 0, activities) {

    override fun getView(position : Int, convertView : View?, parent : ViewGroup) : View {
        val dataActivity = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_item, parent, false)

        view.activity_time.text = "${dataActivity.startTime} - ${dataActivity.endTime}"
        view.activity_name.text = dataActivity.activity

        return view
    }

}