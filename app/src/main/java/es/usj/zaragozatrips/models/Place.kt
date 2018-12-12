package es.usj.zaragozatrips.models

/**
 * Created by libranner on 12/12/2018.
 */
class Place(val name: String, val tipo: String,
            val schedule: String, val coordinates: Coordinate, val videUrl: String,
                 val imagesUrl: Array<String>, val description: String)