package com.dmitriisalenko.gfit.activities.activitiesgfit.data

data class DataBucket(
    var date: Long
) {
    var activities: List<DataActivity>? = null
}