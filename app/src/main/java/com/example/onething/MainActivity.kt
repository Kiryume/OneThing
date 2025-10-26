package com.example.onething

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onething.data.AppDatabase
import com.example.onething.ui.TaskListScreen
import com.example.onething.ui.TaskViewModel
import com.example.onething.ui.TaskViewModelFactory
import com.example.onething.ui.theme.OneThingTheme
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val dao = db.taskDao()
        val factory = TaskViewModelFactory(applicationContext, dao)

        setContent {
            OneThingTheme {
                val viewModel: TaskViewModel = viewModel(factory = factory)
                val tasks = viewModel.tasks.collectAsState(initial = emptyList()).value

                TaskListScreen(tasks = tasks, onAddTask = { viewModel.addTask(it) })
            }
        }
    }
}
