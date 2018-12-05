package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.app.Activity
import android.arch.lifecycle.ViewModel
import com.google.android.gms.fitness.data.DataType

class MainViewModel : ViewModel() {
    var initialized : Boolean = false

    var isPlayServicesAvailable: Boolean = false
    var hasPermission : Boolean = false

    var requestPermissionStatusCode = Activity.RESULT_OK

    var error : String? = null



    // new structure
    var processing : Boolean = false

    var locationPermission : Boolean = false
    var locationPermissionError: String? = null

    var googleFitPermissions : Boolean = false
    var googleFitPermissionsError : String? = null

    var subscriptionStatus = hashMapOf(
        DataType.TYPE_ACTIVITY_SAMPLES to false,
        DataType.TYPE_ACTIVITY_SEGMENT to false,
        DataType.AGGREGATE_ACTIVITY_SUMMARY to false,

        DataType.TYPE_DISTANCE_DELTA to false,
        DataType.TYPE_DISTANCE_CUMULATIVE to false,
        DataType.AGGREGATE_DISTANCE_DELTA to false,

        DataType.TYPE_STEP_COUNT_DELTA to false,
        DataType.TYPE_STEP_COUNT_CADENCE to false,
        DataType.TYPE_STEP_COUNT_CUMULATIVE to false,

        DataType.TYPE_LOCATION_TRACK to false,
        DataType.TYPE_LOCATION_SAMPLE to false
    )
}
