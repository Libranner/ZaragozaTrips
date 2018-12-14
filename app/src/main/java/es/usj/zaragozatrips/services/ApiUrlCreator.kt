package es.usj.zaragozatrips.services

/**
 * Created by libranner on 09/12/2018.
 */
class ApiUrlCreator {
    companion object {
        private const val BASE_URL = "https://firebasestorage.googleapis.com/v0/b/test-5596f.appspot.com/o/"
        private const val QUERY_STRING = "?alt=media&token=880e072d-bde4-46f2-83f5-e31e36d5c8b7"

        fun createURL(path: String): String {
            return "$BASE_URL$path$QUERY_STRING".replace(" ".toRegex(), "%20")
        }
    }
}