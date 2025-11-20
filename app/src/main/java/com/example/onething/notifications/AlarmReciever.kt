package com.example.onething.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmScheduler.ACTION_SHOW_INITIAL_NOTIFICATION -> {
                checkAndShowNotification(context)
                AlarmScheduler.scheduleRepeatingCheck(context)
            }

            AlarmScheduler.ACTION_CHECK_NOTIFICATION_STATUS -> {
                if (isConditionMet(context)) {
                    AlarmScheduler.cancelRepeatingCheck(context)
                    NotificationHandler.cancelNotification(context)
                } else {
                    checkAndShowNotification(context)
                }
            }

            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_LOCKED_BOOT_COMPLETED, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> {
                AlarmScheduler.scheduleInitialAlarm(context)
            }
        }
    }

    private fun checkAndShowNotification(context: Context) {
        if (!NotificationHandler.isNotificationActive(context)) {
            NotificationHandler.showNotification(context)
        }
    }

    private fun isConditionMet(context: Context): Boolean {
        return false
    }
}
