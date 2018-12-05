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

    // new structure
    var locationPermission : Boolean = false
    var locationPermissionError: String? = null

    var googleFitPermissions : Boolean = false
    var googleFitPermissionsError : String? = null
}
