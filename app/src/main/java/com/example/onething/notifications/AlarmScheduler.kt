package com.example.onething.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import java.util.Calendar

object AlarmScheduler {

    const val ACTION_SHOW_INITIAL_NOTIFICATION = "com.example.myapp.ACTION_SHOW_INITIAL_NOTIFICATION"
    const val ACTION_CHECK_NOTIFICATION_STATUS = "com.example.myapp.ACTION_CHECK_NOTIFICATION_STATUS"

    private const val FIVE_MINUTES_MS = 5 * 60 * 1000L

    fun scheduleInitialAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SHOW_INITIAL_NOTIFICATION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Set the alarm to trigger at 7:00 AM
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // If 7 AM has already passed today, schedule it for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        Log.d("AlarmScheduler", "Setting 7 AM alarm for: ${calendar.time}")

        // Use setExactAndAllowWhileIdle for exact timing
        // This requires SCHEDULE_EXACT_ALARM permission on Android 12+
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (se: SecurityException) {
            Log.e("AlarmScheduler", "SecurityException: Missing SCHEDULE_EXACT_ALARM permission?")
            // Fallback or notify user
        }
    }

    fun scheduleRepeatingCheck(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_CHECK_NOTIFICATION_STATUS
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1, // Use a different request code
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        Log.d("AlarmScheduler", "Setting 5-minute repeating check")

        // setInexactRepeating is more battery-friendly.
        // If you need exact 5-minute intervals, you'd have to use setExactAndAllowWhileIdle
        // and re-schedule it every time in the receiver, which is more complex.
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + FIVE_MINUTES_MS,
            FIVE_MINUTES_MS,
            pendingIntent
        )
    }

    fun cancelRepeatingCheck(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_CHECK_NOTIFICATION_STATUS
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1, // Must match the request code used for scheduling
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("AlarmScheduler", "Canceled 5-minute repeating check")
        }
    }
}
