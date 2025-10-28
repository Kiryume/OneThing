package com.example.onething.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.onething.R

object NotificationChannels {
    const val CHANNEL_ID = "OneThingChannel"

    fun ensureCreated(context: android.content.Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existing = notificationManager.getNotificationChannel(CHANNEL_ID)
        if (existing != null) {
            return
        }
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val myChannel = NotificationChannel(
            CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(myChannel)

    }
}