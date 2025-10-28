package com.example.onething.workers

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.example.onething.widget.TasksWidget

class WidgetSyncWorker(
    val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        TasksWidget().updateAll(context)
        return Result.success()
    }

    companion object {
        fun fireWorker(context: Context) {
            val workManager = WorkManager.getInstance(context)
            val notificationRequest: WorkRequest =
                OneTimeWorkRequestBuilder<WidgetSyncWorker>().build()
            workManager.enqueue(notificationRequest)

        }
    }
}