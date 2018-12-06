package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataActivity

class ActivityItemView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr : Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    var dataActivity : DataActivity? = null
}