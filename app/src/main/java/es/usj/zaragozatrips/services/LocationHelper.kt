package es.usj.zaragozatrips.services

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import es.usj.zaragozatrips.models.Coordinate
import es.usj.zaragozatrips.models.Place

object LocationHelper {
    lateinit var locationManager: LocationManager
    var lastVisitedPlace: Place? = null
    var lastPlaceRecommended: Place? = null

    @SuppressLint("MissingPermission")
    fun lastLocation() : Location {
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    fun calculateDistance(coordinate: Coordinate): Double {
        val result = FloatArray(1)
        Location.distanceBetween(coordinate.latitude, coordinate.longitude,
                lastLocation().latitude, lastLocation().longitude, result)
        val meters = result[0]/ 1000.0
        return meters
    }
}