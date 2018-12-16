package es.usj.zaragozatrips.services

import android.util.Log
import com.google.gson.Gson
import es.usj.zaragozatrips.models.Coordinate
import es.usj.zaragozatrips.models.CustomPlace
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by libranner on 13/12/2018.
 */
object CustomDataManager {
    var places: ArrayList<CustomPlace> = arrayListOf()
    val filename = "custom_places.json"
    private lateinit var directory: File

    fun findPlace(id: UUID) : CustomPlace? {
        return places.find {
            it.id == id
        }
    }

    fun saveNewCustomPlace(place: CustomPlace): Boolean {
        try {
            val location = LocationHelper.lastLocation()
            val coordinate = Coordinate(location.latitude,location.longitude, 0.0)
            place.coordinate = coordinate

            places.add(place)
            writeJson()
        }
        catch(e: Exception) {
            Log.e("Error: ", e.toString())
            return false
        }
        return true
    }

    fun updatePlace(place: CustomPlace) {
        val result = places.find {
            it.id == place.id
        }

        if(result != null) {
            removePlace(result)
            saveNewCustomPlace(place)
        }
        writeJson()
    }

    fun initialize(directory: File) {
        this.directory = directory
        readJson()
    }

    fun removePlace(place: CustomPlace) {
        places.remove(place)
        writeJson()
    }

    private fun readJson(): Array<CustomPlace>  {
        val gson = Gson()
        val file = File(directory, filename)
        if(file.exists()) {
            places = gson.fromJson(file.readText(), Array<CustomPlace>::class.java).toCollection(ArrayList())
        }
        return places.toTypedArray()
    }

    private fun writeJson() {
        val file = File(directory, filename)

        if(!file.exists()) {
            file.createNewFile()
        }

        val gson = Gson()
        file.writeText(gson.toJson(places))
    }

    fun getData(onDataReady: ((places: Array<CustomPlace>) -> Unit)?) {
        readJson()
        if(onDataReady != null) {
            onDataReady(places.toTypedArray())
        }
    }
}