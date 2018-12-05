package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.dmitriisalenko.gfit.activities.activitiesgfit.R
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataBucket
import kotlinx.android.synthetic.main.bucket_item.view.*

class BucketAdapter(
    private val ctx : Context,
    private val buckets : ArrayList<DataBucket>
) : ArrayAdapter<DataBucket>(ctx, 0, buckets) {

    override fun getView(position : Int, convertView : View?, parent : ViewGroup) : View {
        val bucket = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.bucket_item, parent, false)

        view.bucket_date.text = bucket.date
        view.bucket_activities.adapter = ActivityAdapter(context, bucket.activities as ArrayList)

        return view
    }

}