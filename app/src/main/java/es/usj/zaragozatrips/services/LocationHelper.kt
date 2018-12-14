package es.usj.zaragozatrips.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import es.usj.zaragozatrips.models.Place

object LocationHelper {
    private lateinit var locationManager: LocationManager
    var lastVisitedPlace: Place? = null
    var lastPlaceRecommended: Place? = null


    @SuppressLint("MissingPermission")
    fun lastLocation() : Location {
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    fun askLocationPermission(manager: LocationManager, activity: Activity) {
        locationManager = manager
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                        if(report.areAllPermissionsGranted()){
                            setupLocationListener(manager)
                        }else{
                            Toast.makeText(activity, "All permissions need to be granted to get location", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {
                        //show alert dialog with permission options
                        AlertDialog.Builder(activity)
                                .setTitle(
                                        "Permissions Error!")
                                .setMessage(
                                        "Please allow permissions to get location")
                                .setNegativeButton(
                                        android.R.string.cancel
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                    token?.cancelPermissionRequest()
                                }
                                .setPositiveButton(android.R.string.ok
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                    token?.continuePermissionRequest()
                                }
                                .setOnDismissListener {
                                    token?.cancelPermissionRequest() }
                                .show()
                    }
                }).check()
    }

    @SuppressLint("MissingPermission")
    fun setupLocationListener(manager: LocationManager) {

        // Define a listener that responds to location updates
        val locationListener = object : LocationListener {

            override fun onLocationChanged(location: Location) {
                // Called when a new location is found by the network location provider.
                checkUserLocation(location)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                0f,locationListener)
    }

    private fun checkUserLocation(location: Location) {
        Log.v("Location: ", location.toString())

        if(lastVisitedPlace != null) {
            val result = FloatArray(1)
            Location.distanceBetween(lastVisitedPlace!!.coordinate.latitude,
                    lastVisitedPlace!!.coordinate.longitude,
                    location.latitude, location.longitude, result)

            val meters = result[0] / 1000.0
            if (meters > lastVisitedPlace!!.coordinate.radio) {
                //Fire Notification to Show Near Places
                lastVisitedPlace = null
            }
        }
        else {
            val places = DataManager.places
            if(places.isNotEmpty()) {
                val closePlace = places.find {
                    it.isCloseTo(location)
                }

                if(closePlace?.name != lastPlaceRecommended?.name) {
                    //Fire Notification Close Place to Visit
                }
            }
        }
    }
}