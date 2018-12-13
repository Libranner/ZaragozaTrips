package es.usj.zaragozatrips.models

import java.util.*

/**
 * Created by libranner on 13/12/2018.
 */

data class CustomPlace(
        val id: UUID,
        val name: String,
        val description: String,
        val coordinate: Coordinate,
        val videosUrl: Array<String>,
        val imagesUrl: Array<String>
        )