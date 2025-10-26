package com.example.onething

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.onething.data.AppDatabase
import com.example.onething.ui.TaskListScreen
import com.example.onething.ui.TaskViewModel
import com.example.onething.ui.TaskViewModelFactory
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "tasks.db"
        ).build()

        val dao = db.taskDao()
        val factory = TaskViewModelFactory(dao)

        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                val viewModel: TaskViewModel = viewModel(factory = factory)
                val tasks = viewModel.tasks.collectAsState(initial = emptyList()).value

                TaskListScreen(tasks = tasks, onAddTask = { viewModel.addTask(it) })
            }
        }
    }
}
