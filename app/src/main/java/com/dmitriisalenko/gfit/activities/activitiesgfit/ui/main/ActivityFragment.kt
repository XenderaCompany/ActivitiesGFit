package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmitriisalenko.gfit.activities.activitiesgfit.R
import kotlinx.android.synthetic.main.activity_fragment.*

class ActivityFragment : Fragment() {
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
    }

    private fun render() {
        activity_loading.visibility = View.GONE

        activity_type.text = viewModel.selectedActivity?.activity
    }
}