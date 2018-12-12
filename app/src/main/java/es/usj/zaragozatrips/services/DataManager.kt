package es.usj.zaragozatrips.services

import es.usj.song_quiz.services.ApiUrlCreator
import es.usj.song_quiz.services.AsyncTaskJsonHandler
import es.usj.zaragozatrips.models.Coordinate
import es.usj.zaragozatrips.models.Place
import org.json.JSONArray

/**
 * Created by libranner on 12/12/2018.
 */
object DataManager {
    var places: ArrayList<Place> = arrayListOf()
    var onDataReady: ((places: Array<Place>) -> Unit)? = null

    fun getData(onDataReady: ((places: Array<Place>) -> Unit)?) {
        this.onDataReady = onDataReady
        AsyncTaskJsonHandler(::handlerJson).execute(ApiUrlCreator.createURL("places.json"))
    }

    fun findPlace(coordinate: Coordinate) : Place?{
        return places.find {
            it.coordinate.latitude == coordinate.latitude
                    && it.coordinate.longitude == coordinate.longitude
        }
    }

    fun filterPlaces(type: String?): Array<Place>? {
        if(type == null){
            return places.toTypedArray()
        }

        return places.filter { it.type == type }.toTypedArray()
    }

    private fun handlerJson(result: String?) {
        val jsonArray = JSONArray(result)

        var x = 0
        while (x < jsonArray.length()) {
            val jsonObject =  jsonArray.getJSONObject(x)

            val jsonCoordinate =  jsonObject.getString("coordinate").split(",")
            val coordinate = Coordinate(jsonCoordinate[0].toDouble(), jsonCoordinate[1].toDouble(),
                    jsonCoordinate[2].toDouble())

            places.add(Place(
                    jsonObject.getString("name"),
                    jsonObject.getString("type"),
                    jsonObject.getString("schedule"),
                    coordinate,
                    jsonObject.getString("videoUrl"),
                    jsonObject.getString("images").split(",").toTypedArray(),
                    jsonObject.getString("website"),
                    jsonObject.getString("description")
            ))

            x++
        }

        if(onDataReady != null) {
            onDataReady!!(places.toTypedArray())
        }
    }
}