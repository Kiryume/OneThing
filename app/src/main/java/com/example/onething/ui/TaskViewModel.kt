package com.example.onething.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onething.data.Task
import com.example.onething.data.TaskDao
import com.example.onething.widget.TasksWidget
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(private val appContext: Context, private val dao: TaskDao) : ViewModel() {
    val tasks = dao.getAllToday()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addTask(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                dao.insert(Task(name = name))
                TasksWidget.updateAll(appContext)
            }
        }
    }
}

class TaskViewModelFactory(private val appContext: Context, private val dao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(appContext, dao) as T
    }
}
