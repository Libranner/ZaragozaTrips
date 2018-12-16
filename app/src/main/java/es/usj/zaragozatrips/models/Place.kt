package es.usj.zaragozatrips.models

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import es.usj.zaragozatrips.services.LocationHelper
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by libranner on 12/12/2018.
 */
@Parcelize
class Place(
        val name: String,
        val type: String,
        val schedule: String,
        val coordinate: Coordinate,
        val videoUrl: String,
        val imagesUrl: Array<String>,
        val website: String,
        val description: String): Parcelable {


    fun isCloseTo(location: Location) : Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(coordinate.latitude, coordinate.longitude,
                location.latitude, location.longitude, result)

        val meters = result[0] / 1000.0
        return (meters <= coordinate.radio)
    }
}

