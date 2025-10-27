package com.example.onething

import android.app.Application
import android.app.NotificationChannel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.onething.workers.MorningNotificationWorker

class OneThingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupNotifications()

    }

    fun setupNotifications() {

        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val myChannel = NotificationChannel(
            CHANNEL_ID,
            name,
            android.app.NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = descriptionText
        }
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(myChannel)

        val workManager = WorkManager.getInstance(applicationContext)
        val notificationRequest: WorkRequest =
            OneTimeWorkRequestBuilder<MorningNotificationWorker>().build()
        workManager.enqueue(notificationRequest)


    }

    companion object {
        const val CHANNEL_ID = "OneThingChannel"
    }
}