package es.usj.zaragozatrips.services

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import es.usj.zaragozatrips.MenuActivity
import es.usj.zaragozatrips.R
import es.usj.zaragozatrips.models.Place

object NotificationHelper {

    const val CHANNEL_ID = "es.usj.zaragozatrips"
    const val NOTIFICATION_ID = 1001
    const val PLACE_KEY_ID = "PLACE_KEY"
    const val SHOW_NEAR_PLACES_KEY_ID = "SHOW_NEAR_PLACES_KEY_ID"
    var notificationManager: NotificationManager? = null

    fun send (context: Activity, title: String, content: String, place: Place?) {

        val intent = Intent(context, MenuActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        if(place != null) {
            intent.putExtra(PLACE_KEY_ID, place)
        }

        setupNotification(context, intent, title, content)
    }

    fun send (context: Activity, title: String, content: String, showNearPlaces: Boolean) {

        val intent = Intent(context, MenuActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra(SHOW_NEAR_PLACES_KEY_ID, showNearPlaces)

        setupNotification(context, intent, title, content)
    }

    private fun setupNotification(context: Activity, intent: Intent, title: String, content: String) {
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, (Math.random() * 100).toInt(), intent, 0)
        PendingIntent.FLAG_UPDATE_CURRENT

        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, mBuilder.build())
        }
    }

    fun createNotificationChannel(channelName: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }
}