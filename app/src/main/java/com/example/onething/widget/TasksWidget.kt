package com.example.onething.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import com.example.onething.MainActivity
import com.example.onething.data.AppDatabase
import com.example.onething.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val tasks: List<Task> = withContext(Dispatchers.IO) {
            AppDatabase.getInstance(context).taskDao().getAllOnce()
        }
        provideContent { TasksWidgetContent(context, tasks) }
    }

    companion object {
        suspend fun updateAll(context: Context) {
            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(TasksWidget::class.java)
            val widget = TasksWidget()
            for (id in ids) {
                widget.update(context, id)
            }
        }
    }
}

@Composable
private fun TasksWidgetContent(context: Context, tasks: List<Task>) {
    val intent = Intent(context, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

    GlanceTheme {
        val titleStyle = TextStyle(
            color = GlanceTheme.colors.onBackground,
            fontWeight = FontWeight.Bold
        )
        val bodyStyle = TextStyle(color = GlanceTheme.colors.onBackground)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(GlanceTheme.colors.background)
                .clickable(
                    onClick = actionStartActivity(intent)
                )
                .padding(12.dp),
        ) {
            Text(text = "My Tasks", style = titleStyle)
            Spacer(modifier = GlanceModifier.height(8.dp))
            if (tasks.isEmpty()) {
                Text(text = "No tasks yet", style = bodyStyle)
            } else {
                val shown = tasks.take(5)
                for (t in shown) Text(text = "• ${t.name}", style = bodyStyle)
                if (tasks.size > shown.size) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(text = "+${tasks.size - shown.size} more…", style = bodyStyle)
                }
            }
        }
    }
}
