package es.usj.zaragozatrips.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import android.content.Context.LOCATION_SERVICE
import android.util.Log


class LocationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        LocationHelper.locationManager = locationManager

        setupLocationListener(locationManager, applicationContext)
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationListener(manager: LocationManager, activity: Context) {

        // Define a listener that responds to location updates
        val locationListener = object : LocationListener {

            override fun onLocationChanged(location: Location) {
                // Called when a new location is found by the network location provider.
                Log.e("Location: ", location.toString())
                checkUserLocation(location, activity)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000,
                0f,locationListener)
    }

    private fun checkUserLocation(location: Location, activity: Context) {
        if(LocationHelper.lastVisitedPlace != null) {
            val result = FloatArray(1)
            Location.distanceBetween(LocationHelper.lastVisitedPlace!!.coordinate.latitude,
                    LocationHelper.lastVisitedPlace!!.coordinate.longitude,
                    location.latitude, location.longitude, result)

            val meters = result[0] / 1000.0
            if (meters > LocationHelper.lastVisitedPlace!!.coordinate.radio) {
                //Fire Notification to Show Near Places
                NotificationHelper.send(activity, "Keep it up!", "There are more places to see", true)
                LocationHelper.lastVisitedPlace = null
            }
        }
        else {
            val places = DataManager.places
            if(places.isNotEmpty()) {
                val closePlace = places.find {
                    it.isCloseTo(location)
                }

                if(closePlace?.name != LocationHelper.lastPlaceRecommended?.name) {
                    //Fire Notification Close Place to Visit
                    NotificationHelper.send(activity, "Place info", "Let us show you a cool place nearby", closePlace)
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
