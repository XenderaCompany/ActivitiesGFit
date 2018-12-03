package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.arch.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var initialized : Boolean = false

    var isPlayServicesAvailable: Boolean = false
    var hasPermission : Boolean = false

    var requestPermissionStatusCode = 0
    var requestPermissionStatusMessage = ""

    var error : String? = null

    var processing : Boolean = false
}
