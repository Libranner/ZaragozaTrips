package es.usj.zaragozatrips.services

import es.usj.song_quiz.services.ApiUrlCreator
import es.usj.song_quiz.services.AsyncTaskJsonHandler
import es.usj.zaragozatrips.models.Coordinate
import es.usj.zaragozatrips.models.CustomPlace
import org.json.JSONArray
import java.util.*

/**
 * Created by libranner on 13/12/2018.
 */
object CustomDataManager {
    var places: ArrayList<CustomPlace> = arrayListOf()
    var onDataReady: ((places: Array<CustomPlace>) -> Unit)? = null

    fun saveNewCustomPlace(place: CustomPlace) {

    }

    fun updatePlace(place: CustomPlace) {

    }

    fun removePlace(place: CustomPlace) {

    }

    fun getData(onDataReady: ((places: Array<CustomPlace>) -> Unit)?) {
        this.onDataReady = onDataReady
        AsyncTaskJsonHandler(::handlerJson).execute(ApiUrlCreator.createURL("places.json"))
    }

    private fun handlerJson(result: String?) {
        val jsonArray = JSONArray(result)

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
                    jsonObject.getString("videoUrl").split(",").toTypedArray(),
                    jsonObject.getString("images").split(",").toTypedArray()
            ))
            x++
        }

        if(onDataReady != null) {
            onDataReady!!(places.toTypedArray())
        }
    }
}