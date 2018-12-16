package es.usj.zaragozatrips.services

import android.app.Activity
import android.net.Uri
import android.provider.MediaStore

object UriHelper {
    fun getRealPathFromURI(activity: Activity, uri: Uri?): String {
        var path = ""
        if (activity.contentResolver != null && uri != null) {
            val cursor = activity.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }
}