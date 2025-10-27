package com.example.onething.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import com.example.onething.MainActivity
import com.example.onething.data.AppDatabase
import com.example.onething.data.Task
import com.example.onething.ui.AddTaskActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val context = LocalContext.current
            val dao = remember { AppDatabase.getInstance(context).taskDao() }

            val tasks: List<Task>? by dao.getAllToday().collectAsState(initial = null)

            GlanceTheme {
                when(tasks) {
                    null -> {
                        Box(
                            modifier = GlanceModifier
                                .fillMaxSize()
                                .appWidgetBackground()
                                .background(GlanceTheme.colors.widgetBackground)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GlanceTheme.colors.onBackground)
                        }
                    }
                    else -> {
                        TasksWidgetContent(context = context, tasks = tasks!!)
                    }
                }
            }
        }
    }

    @Composable
    private fun TasksWidgetContent(context: Context, tasks: List<Task>) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val addTaskIntent = Intent(context, AddTaskActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val titleStyle = TextStyle(
            color = GlanceTheme.colors.onBackground,
            fontWeight = FontWeight.Bold
        )
        val bodyStyle = TextStyle(color = GlanceTheme.colors.onBackground)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp),
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.End
            ) {
                Text(text = "My Tasks", style = titleStyle)
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = "Add", style = titleStyle, modifier = GlanceModifier
                        .clickable(onClick = actionStartActivity(addTaskIntent))
                )
            }
            Spacer(modifier = GlanceModifier.height(8.dp))
            LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                if (tasks.isEmpty()) {
                    item {
                        Text(
                            text = "No tasks yet!",
                            style = bodyStyle,
                            modifier = GlanceModifier.padding(top = 8.dp)
                        )
                    }
                } else {
                    items(tasks.size) { i ->
                        Text(text = "• ${tasks[i].name}", style = bodyStyle)
                    }
                }
            }
        }
    }
}