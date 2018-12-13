package es.usj.zaragozatrips.services

import com.google.gson.Gson
import es.usj.song_quiz.services.ApiUrlCreator
import es.usj.song_quiz.services.AsyncTaskJsonHandler
import es.usj.zaragozatrips.models.Coordinate
import es.usj.zaragozatrips.models.CustomPlace
import org.json.JSONArray
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by libranner on 13/12/2018.
 */
object CustomDataManager {
    var places: ArrayList<CustomPlace> = arrayListOf()
    var onDataReady: ((places: Array<CustomPlace>) -> Unit)? = null
    val filename = "custom_places.json"
    private lateinit var directory: File

    fun saveNewCustomPlace(place: CustomPlace) {
        places.add(place)
        writeJson()
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
        this.onDataReady = onDataReady
        AsyncTaskJsonHandler(::handlerJson).execute(ApiUrlCreator.createURL("places.json"))
    }

    private fun handlerJson(result: String?) {
        /*val jsonArray = JSONArray(result)

        var x = 0
        while (x < jsonArray.length()) {
            val jsonObject =  jsonArray.getJSONObject(x)

            val jsonCoordinate =  jsonObject.getString("coordinate").split(",")
            val coordinate = Coordinate(jsonCoordinate[0].toDouble(), jsonCoordinate[1].toDouble(),
                    jsonCoordinate[2].toDouble())

            val id = UUID.fromString(jsonObject.getString("id"))
            places.add(CustomPlace(
                id,
                jsonObject.getString("name"),
                jsonObject.getString("description"),
                coordinate,
                jsonObject.getString("videoUrl").split(",").toMutableList(),
                jsonObject.getString("images").split(",").toMutableList()
            ))
            x++
        }*/

        readJson()

        if(onDataReady != null) {
            onDataReady!!(places.toTypedArray())
        }
    }
}