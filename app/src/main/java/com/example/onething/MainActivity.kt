package com.example.onething

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onething.data.AppDatabase
import com.example.onething.ui.TaskListScreen
import com.example.onething.ui.TaskViewModel
import com.example.onething.ui.TaskViewModelFactory
import androidx.compose.runtime.collectAsState
import com.example.onething.ui.AddTaskActivity
import com.example.onething.ui.theme.OneThingTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPermissions()

        val db = AppDatabase.getInstance(applicationContext)
        val dao = db.taskDao()
        val factory = TaskViewModelFactory(applicationContext, dao)

        setContent {
            OneThingTheme {
                val viewModel: TaskViewModel = viewModel(factory = factory)
                val tasks = viewModel.tasks.collectAsState(initial = emptyList()).value
                TaskListScreen(
                    tasks = tasks,
                    onAddClicked = {
                        startActivity(Intent(this, AddTaskActivity::class.java))
                    }
                )
            }
        }
    }

    fun setupPermissions() {
        var showRequest =
            shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)
        if (!showRequest) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }
        showRequest = shouldShowRequestPermissionRationale(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
        if (!showRequest) {
            requestPermissions(arrayOf(android.Manifest.permission.SCHEDULE_EXACT_ALARM), 102)
        }
    }
}
