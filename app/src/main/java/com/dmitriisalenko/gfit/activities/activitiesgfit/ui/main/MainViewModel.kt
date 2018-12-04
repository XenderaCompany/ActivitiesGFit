package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.app.Activity
import android.arch.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var initialized : Boolean = false

    var isPlayServicesAvailable: Boolean = false
    var hasPermission : Boolean = false

    var requestPermissionStatusCode = Activity.RESULT_OK

    var error : String? = null

    var processing : Boolean = false
}
