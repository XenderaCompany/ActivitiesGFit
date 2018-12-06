package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.dmitriisalenko.gfit.activities.activitiesgfit.R
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataActivity
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataBucket
import kotlinx.android.synthetic.main.bucket_item.view.*
import java.text.DateFormat

class BucketAdapter(
    private val ctx : Context,
    private val buckets : ArrayList<DataBucket>,
    private val onItemClick: AdapterView.OnItemClickListener
) : ArrayAdapter<DataBucket>(ctx, 0, buckets) {

    override fun getView(position : Int, convertView : View?, parent : ViewGroup) : View {
        val bucket = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.bucket_item, parent, false)

        val activities = bucket.activities as ArrayList

        activities.reverse()

        val dateFormat = DateFormat.getDateInstance()

        view.bucket_date.text = dateFormat.format(bucket.date)
        view.bucket_activities.adapter = ActivityAdapter(context, activities)
        view.bucket_activities.onItemClickListener = onItemClick

        return view
    }

}