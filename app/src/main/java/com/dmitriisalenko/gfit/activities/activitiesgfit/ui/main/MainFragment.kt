package com.dmitriisalenko.gfit.activities.activitiesgfit.ui.main

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.dmitriisalenko.gfit.activities.activitiesgfit.R
import com.dmitriisalenko.gfit.activities.activitiesgfit.R.id.*
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.auth.api.signin.GoogleSignIn.hasPermissions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.common.util.ScopeUtil
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    private val REQUEST_CODE = 2018
    private val LOCATION_REQUEST_CODE = 2019

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        render()

        // set up view model
        viewModel.isPlayServicesAvailable = isPlayServicesAvailable()

        if (isPlayServicesAvailable()) {
            viewModel.hasPermission = hasPermissions()
        }

        viewModel.initialized = true

        render()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

            viewModel.processing = false
            viewModel.hasPermission = resultCode == Activity.RESULT_OK
            viewModel.requestPermissionStatusCode = resultCode

            if (viewModel.hasPermission) {
                enableRecordingApi()
            }

            render()
        }
    }

    private fun isPlayServicesAvailable() : Boolean = GoogleApiAvailability
        .getInstance()
        .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

    private fun hasPermissions() : Boolean = GoogleSignIn.hasPermissions(
        GoogleSignIn.getLastSignedInAccount(context),
        Scope(Scopes.FITNESS_ACTIVITY_READ),
        Scope(Scopes.FITNESS_LOCATION_READ)
    )

    private fun requestPermissions() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.processing = true

        render()

        GoogleSignIn.requestPermissions(
            this,
            REQUEST_CODE,
            GoogleSignIn.getLastSignedInAccount(context),
            Scope(Scopes.FITNESS_ACTIVITY_READ),
            Scope(Scopes.FITNESS_LOCATION_READ)
        )
    }

    private fun disconnectGoogleFit() {
        val context = context
        val gsa = GoogleSignIn.getLastSignedInAccount(context)

        if (context == null || gsa == null) {
            return
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.processing = true

        render()

        Fitness
            .getConfigClient(context, gsa)
            .disableFit()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewModel.hasPermission = false
                } else {
// if we have SignIn Required error here, how to handle it correctly?
//                    val gsio = GoogleSignInOptions.Builder()
//                        .requestScopes(Scope(Scopes.FITNESS_ACTIVITY_READ), Scope(Scopes.FITNESS_ACTIVITY_READ))
//                        .build()
//                    val client = GoogleSignIn.getClient(context, gsio)
//                    val intent = client.signInIntent
//                    activity?.startActivityForResult(intent, REQUEST_CODE)
                    viewModel.error = it.exception.toString()
                }

                viewModel.processing = false

                render()
            }
    }

    private fun enableRecordingApi() {
        val ctx = context
        val gsa = GoogleSignIn.getLastSignedInAccount(ctx)

        if (ctx == null || gsa == null) {
            return
        }

        if (ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            Log.v("Subscription failure", "Subscription failure: ACCESS_FINE_LOCATION is not granted")
            activity?.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }

        listOf(
            DataType.TYPE_ACTIVITY_SAMPLES,
            DataType.TYPE_ACTIVITY_SEGMENT,
            DataType.TYPE_DISTANCE_DELTA,
            DataType.TYPE_DISTANCE_CUMULATIVE,
            DataType.TYPE_STEP_COUNT_DELTA,
            DataType.TYPE_STEP_COUNT_CADENCE,
            DataType.TYPE_STEP_COUNT_CUMULATIVE,
            DataType.TYPE_LOCATION_SAMPLE,
            DataType.TYPE_LOCATION_TRACK,

            // aggregated?
            DataType.AGGREGATE_ACTIVITY_SUMMARY,
            DataType.AGGREGATE_DISTANCE_DELTA,
            DataType.AGGREGATE_STEP_COUNT_DELTA
        ).forEach {
            val dt = it.name
            Fitness.getRecordingClient(ctx, gsa)
                .subscribe(it)
                .addOnSuccessListener {
                    Log.v("Success subscription", "Success subscription $dt")
                }
                .addOnFailureListener {
                    val e = it.localizedMessage
                    Log.v("Failure subscription", "Failure subscription $dt $e")
                }
        }
    }

    private fun render() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        error.visibility = View.GONE
        loading.visibility = View.GONE
        no_google_services.visibility = View.GONE
        no_permissions.visibility = View.GONE
        main_layout.visibility = View.GONE

        if (viewModel.error != null) {
            error.visibility = View.VISIBLE
            error_text.text = viewModel.error
            return
        }

        if (!viewModel.initialized || viewModel.processing) {
            loading.visibility = View.VISIBLE
            return
        }

        if (!viewModel.isPlayServicesAvailable) {
            no_google_services.visibility = View.VISIBLE
            return
        }

        if (!viewModel.hasPermission) {
            no_permissions.visibility = View.VISIBLE
            request_permissions
                ?.setOnClickListener { requestPermissions() }

            val statusCode = viewModel.requestPermissionStatusCode
            if (statusCode != Activity.RESULT_OK) {

                if (statusCode == Activity.RESULT_CANCELED) {
                    request_permissions_status.visibility = View.VISIBLE
                    request_permissions_status.text = "Cancelled"
                } else {
                    request_permissions_status.visibility = View.GONE
                }

            } else {
                request_permissions_status.visibility = View.GONE
            }

            return
        }

        disconnect_google_fit
            ?.setOnClickListener { disconnectGoogleFit() }

        main_layout.visibility = View.VISIBLE
    }
}
