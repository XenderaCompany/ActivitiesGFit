package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.dmitriisalenko.gfit.activities.activitiesgfit.MainActivity
import com.dmitriisalenko.gfit.activities.activitiesgfit.R
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataActivity
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataBucket
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.android.synthetic.main.activities_fragment.*
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class ActivitiesFragment : Fragment(), AdapterView.OnItemClickListener {

    companion object {
        fun newInstance() = ActivitiesFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activities_fragment, container, false)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.selectedActivity = (view as ActivityItemView).dataActivity

        (activity as MainActivity).goToActivity()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        init()
    }

    private fun init() {
        readActivities()
    }

    private fun readActivities() {
        val ctx = context
        val gsa = GoogleSignIn.getLastSignedInAccount(ctx)

        ctx ?: return
        gsa ?: return

        val calendar = Calendar.getInstance()
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.MONTH, -3)
        val startTime = calendar.timeInMillis


        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(ctx, gsa)
            .readData(readRequest)
            .addOnSuccessListener {
                viewModel.buckets = it.buckets
                    .filter { bucket -> bucket.dataSets[0].dataPoints.size > 0 }
                    .map { bucket ->
                        val dataBucket = DataBucket(bucket.getStartTime(TimeUnit.MILLISECONDS))
                        dataBucket.activities = bucket.dataSets[0].dataPoints.map { dataPoint ->
                            DataActivity(
                                dataBucket,
                                dataPoint.getValue(Field.FIELD_ACTIVITY).asActivity(),
                                dataPoint.getStartTime(TimeUnit.MILLISECONDS),
                                dataPoint.getEndTime(TimeUnit.MILLISECONDS)
                            )
                        }

                        dataBucket
                    }

                render()
            }
    }

    private fun render() {
        context ?: return

        data_loading.visibility = View.GONE
        data_container.visibility = View.GONE

        if (viewModel.buckets == null) {
            data_loading.visibility = View.VISIBLE

            return
        }

        val buckets = viewModel.buckets!!.reversed() as ArrayList<DataBucket>

        data_container.visibility = View.VISIBLE
        data_container.isRefreshing = false

        data_container.setOnRefreshListener {
            readActivities()
        }

        list_container.adapter = BucketAdapter(context as Context, buckets, this)
    }
}