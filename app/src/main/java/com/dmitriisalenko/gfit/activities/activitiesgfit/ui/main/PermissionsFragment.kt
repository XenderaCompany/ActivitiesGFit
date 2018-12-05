package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.PermissionChecker
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmitriisalenko.gfit.activities.activitiesgfit.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import kotlinx.android.synthetic.main.permissions_fragement.*

class PermissionsFragment : Fragment() {

    private val requestLocationPermissionCode = 1000
    private val requestGoogleFitPermissionsCode = 1001

    companion object {
        fun newInstance() = PermissionsFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.permissions_fragement, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        init()

        render()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestLocationPermissionCode) {
            val resultCode = grantResults[0]

            viewModel.processing = false
            viewModel.locationPermission = resultCode == PermissionChecker.PERMISSION_GRANTED

            if (resultCode != PermissionChecker.PERMISSION_GRANTED) {

                viewModel.locationPermissionError = when (resultCode) {
                    PermissionChecker.PERMISSION_DENIED -> "Permission denied"
                    PermissionChecker.PERMISSION_DENIED_APP_OP -> "Permission denied app op"
                    else -> "Permission denied by unknown reason"
                }
            }

            render()
        } else if (requestCode == requestGoogleFitPermissionsCode) {
            viewModel.processing = false
//            viewModel.googleFitPermissions = resultCode == Activity.RESULT_OK
//
//            if (resultCode != Activity.RESULT_OK) {
//                viewModel.googleFitPermissionsError = when (resultCode) {
//                    Activity.RESULT_CANCELED -> "Cancelled"
//                    else -> "Unknown error"
//                }
//            }

            render()
        }
    }

    private fun init() {
        viewModel.locationPermission = context
            ?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED

        viewModel.googleFitPermissions = GoogleSignIn.hasPermissions(
            GoogleSignIn.getLastSignedInAccount(context),
            Scope(Scopes.FITNESS_ACTIVITY_READ),
            Scope(Scopes.FITNESS_LOCATION_READ)
        )

        request_location_permission.setOnClickListener { requestLocationPermissions() }
    }

    private fun requestLocationPermissions() {
        viewModel.processing = true
        viewModel.locationPermissionError = null

        render()

        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestLocationPermissionCode)
    }

    private fun render() {
        // hide all elements
        listOf(
            location_access
        ).forEach {
            it.visibility = View.GONE
        }

        if (!viewModel.locationPermission) {

            location_access.visibility = View.VISIBLE
            request_location_permission.isEnabled = !viewModel.processing

            request_location_permission_error.text = if (viewModel.locationPermissionError == null)
                "No permissions"
            else
                viewModel.locationPermissionError

            return
        }

        if (!viewModel.googleFitPermissions) {

            step_view.go(1, true)

        }
    }
}