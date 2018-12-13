package es.usj.zaragozatrips.models

import java.util.*

/**
 * Created by libranner on 13/12/2018.
 */

data class CustomPlace(
        val id: UUID,
        var name: String,
        var description: String,
        var coordinate: Coordinate,
        var videosUrl: MutableList<String>,
        var imagesUrl: MutableList<String>
        )