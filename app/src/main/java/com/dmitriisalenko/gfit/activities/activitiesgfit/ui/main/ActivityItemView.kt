package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.dmitriisalenko.gfit.activities.activitiesgfit.data.DataActivity

class ActivityItemView : LinearLayout {
    var dataActivity : DataActivity? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int):
            super(context, attrs, defStyleAttr, defStyleRes)
}