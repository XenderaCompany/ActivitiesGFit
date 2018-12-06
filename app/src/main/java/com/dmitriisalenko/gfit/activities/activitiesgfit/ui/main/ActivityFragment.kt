package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmitriisalenko.gfit.activities.activitiesgfit.MainActivity
import com.dmitriisalenko.gfit.activities.activitiesgfit.R
import com.dmitriisalenko.gfit.activities.activitiesgfit.R.id.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.android.synthetic.main.activity_fragment.*
import java.text.DateFormat
import java.util.concurrent.TimeUnit

class ActivityFragment : Fragment() {
    private var activityLoaded = false
    private var stepsSum: Int? = null
    private var distanceSum : Float? = null

    companion object {
        fun newInstance() = ActivityFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        init()
    }

    private fun init() {
        render()

        readActivityDetails()
    }

    private fun readActivityDetails() {
        val activity = viewModel.selectedActivity

        activity ?: return

        val ctx = context
        val gsa = GoogleSignIn.getLastSignedInAccount(ctx)

        ctx ?: return
        gsa ?: return

        if (activity.activity == "still") {
            activityLoaded = true

            render()
        }

        if (listOf("walking", "running").contains(activity.activity)) Fitness.getHistoryClient(ctx, gsa)
            .readData(DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(activity.startTime, activity.endTime, TimeUnit.MILLISECONDS)
                .build()
            )
            .addOnSuccessListener { readResponse ->
                activityLoaded = true

                val collection = readResponse
                    .getDataSet(DataType.TYPE_STEP_COUNT_DELTA)
                    .dataPoints
                    .map {
                        it.getValue(Field.FIELD_STEPS).asInt()
                    }

                if (collection.isNotEmpty()){
                    stepsSum = collection.reduce { acc, delta -> acc + delta }
                }

                render()

            }
            .addOnFailureListener {
                Log.v("bla bla", "bla bla failure ${it.localizedMessage}")
            }

        if (listOf("walking", "running", "cycling", "in_vehicle").contains(activity.activity)) Fitness.getHistoryClient(ctx, gsa)
            .readData(DataReadRequest.Builder()
                .read(DataType.TYPE_DISTANCE_DELTA)
                .setTimeRange(activity.startTime, activity.endTime, TimeUnit.MILLISECONDS)
                .build()
            )
            .addOnSuccessListener { readResponse ->
                activityLoaded = true

                val collection = readResponse
                    .getDataSet(DataType.TYPE_DISTANCE_DELTA)
                    .dataPoints
                    .map {
                        it.getValue(Field.FIELD_DISTANCE).asFloat()
                    }

                if (collection.isNotEmpty()){
                    distanceSum = collection.reduce { acc, delta -> acc + delta }
                }

                render()
            }
    }

    private fun render() {
        activity_loading.visibility = View.GONE

        val dateFormat = DateFormat.getDateInstance()
        val timeFormat = DateFormat.getTimeInstance()

        back_to_activities.setOnClickListener {
            (activity as MainActivity).goToActivities()
        }

        val activity = viewModel.selectedActivity

        activity ?: return
        activity_type.text = activity.activity
        activity_date.text = dateFormat.format(activity.bucket.date)
        activity_time.text = "${timeFormat.format(activity.startTime)} - ${timeFormat.format(activity.endTime)}"

        if (!activityLoaded) {
            activity_loading.visibility = View.VISIBLE
            activity_info.visibility = View.GONE
        } else {
            activity_loading.visibility = View.GONE
            activity_info.visibility = View.VISIBLE

            if (activity.activity == "still") {
                activity_info.text = "No data for still activity"

                return
            }

            var activityData = ""

            if (stepsSum != null) {
                activityData += "Steps: $stepsSum\n"
            }

            if (distanceSum != null) {
                activityData += "Distance: $distanceSum\n"
            }

            activity_info.text = activityData
        }


    }
}