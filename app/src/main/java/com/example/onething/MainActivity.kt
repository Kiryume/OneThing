package com.example.onething

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onething.data.AppDatabase
import com.example.onething.ui.TaskListScreen
import com.example.onething.ui.TaskViewModel
import com.example.onething.ui.TaskViewModelFactory
import androidx.compose.runtime.collectAsState
import com.example.onething.notifications.NotificationChannels
import com.example.onething.ui.AddTaskActivity
import com.example.onething.ui.theme.OneThingTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import com.example.onething.notifications.AlarmScheduler


class MainActivity : ComponentActivity() {

    private var showAlarmDialog by mutableStateOf(false)
    private var showNotificationDialog by mutableStateOf(false)

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d("MainActivity", "POST_NOTIFICATIONS granted: $granted")
            showNotificationDialog = !granted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationChannels.ensureCreated(this)

        val db = AppDatabase.getInstance(applicationContext)
        val dao = db.taskDao()
        val factory = TaskViewModelFactory(applicationContext, dao)

        updatePermissionStates()

        setContent {
            OneThingTheme {
                val viewModel: TaskViewModel = viewModel(factory = factory)
                val tasks = viewModel.tasks.collectAsState(initial = emptyList()).value

                // Main app UI
                TaskListScreen(
                    tasks = tasks,
                    onAddClicked = {
                        startActivity(Intent(this, AddTaskActivity::class.java))
                    }
                )

                if (showAlarmDialog) {
                    AlarmExplanationDialog(
                        onOpenSettings = {
                            openExactAlarmSettings()
                        },
                        onDismiss = {
                            showAlarmDialog = false
                        }
                    )
                } else if (showNotificationDialog) {
                    NotificationRequestDialog(
                        onRequest = {
                            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        onDismiss = {
                            showNotificationDialog = false
                        }
                    )
                }
            }
        }
    }

    private fun updatePermissionStates() {
        val alarmManager = getSystemService(AlarmManager::class.java)
        val canSchedule = alarmManager?.canScheduleExactAlarms() ?: false
        Log.d("MainActivity", "canScheduleExactAlarms: $canSchedule")
        showAlarmDialog = !canSchedule
        AlarmScheduler.scheduleInitialAlarm(this)

        val notifyPerm = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
        Log.d("MainActivity", "POST_NOTIFICATIONS permission: $notifyPerm")
        showNotificationDialog = (notifyPerm != android.content.pm.PackageManager.PERMISSION_GRANTED)
    }

    private fun openExactAlarmSettings() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            putExtra("android.provider.extra.PACKAGE_NAME", packageName)
        }
        startActivity(intent)
    }
}


@Composable
private fun AlarmExplanationDialog(onOpenSettings: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Allow exact alarms") },
        text = {
            Column {
                Text("This app uses periodic alerts to remind you too plan your day. To ensure these alerts are timely, please allow us to set up alarms and reminders.")
                Spacer(Modifier.height(8.dp))
                Text("You only need to grant this once. If you choose not to, alarms may be delayed by the system.")
            }
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) { Text("Open settings") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Not now") }
        }
    )
}

@Composable
private fun NotificationRequestDialog(onRequest: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notifications") },
        text = { Text("This app uses notifications to remind you about tasks. Please allow notifications.") },
        confirmButton = {
            TextButton(onClick = onRequest) { Text("Allow") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Not now") }
        }
    )
}
