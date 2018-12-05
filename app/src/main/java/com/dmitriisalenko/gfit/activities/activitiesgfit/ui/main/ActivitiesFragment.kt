package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.arch.lifecycle.Transformations.map
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.dmitriisalenko.gfit.activities.activitiesgfit.R
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataActivity
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataBucket
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.android.synthetic.main.activities_fragment.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class ActivitiesFragment : Fragment() {

    companion object {
        fun newInstance() = ActivitiesFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activities_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        init()
    }

    private fun init() {
        val ctx = context
        val gsa = GoogleSignIn.getLastSignedInAccount(ctx)

        if (ctx == null || gsa == null) {
            return
        }

        val calendar = Calendar.getInstance()
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.MONTH, -3)
        val startTime = calendar.timeInMillis

        val dateFormat = DateFormat.getDateInstance()
        val timeFormat = DateFormat.getTimeInstance()

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(ctx, gsa)
            .readData(readRequest)
            .addOnSuccessListener {
                viewModel.activities = it.buckets
                    .filter { bucket -> bucket.dataSets.size > 0 }
                    .map { bucket ->
                        DataBucket(
                            dateFormat.format(bucket.getStartTime(TimeUnit.MILLISECONDS)),
                            bucket.dataSets[0].dataPoints.map { dataPoint ->
                                DataActivity(
                                    dataPoint.getValue(Field.FIELD_ACTIVITY).asActivity(),
                                    timeFormat.format(dataPoint.getStartTime(TimeUnit.MILLISECONDS)),
                                    timeFormat.format(dataPoint.getEndTime(TimeUnit.MILLISECONDS))
                                )
                            }
                        )
                    }

                render()
            }
    }

    private fun render() {
        context ?: return

        data_loading.visibility = View.GONE
        data_container.visibility = View.GONE

        if (viewModel.activities == null) {
            data_loading.visibility = View.VISIBLE

            return
        }



        data_container.visibility = View.VISIBLE
        list_container.adapter = BucketAdapter(context as Context, viewModel.activities!! as ArrayList<DataBucket> )
    }
}