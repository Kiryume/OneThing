package com.example.onething.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.onething.data.AppDatabase
import com.example.onething.data.Task
import com.example.onething.ui.theme.OneThingTheme
import com.example.onething.widget.TasksWidget
import com.example.onething.workers.WidgetSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Draw behind system bars so IME insets work nicely with imePadding
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        setContent {
            OneThingTheme {
                AddTaskOverlay(
                    onSave = { name ->
                        val trimmed = name.trim()
                        if (trimmed.isNotEmpty()) {
                            val appCtx = applicationContext
                            val dao = AppDatabase.getInstance(appCtx).taskDao()
                            lifecycleScope.launch(Dispatchers.IO) {
                                dao.insert(Task(name = trimmed))
                                WidgetSyncWorker.fireWorker(appCtx)
                                withContext(Dispatchers.Main) { finish() }
                            }
                        } else {
                            finish()
                        }
                    },
                    onCancel = { finish() }
                )
            }
        }
    }
}

@Composable
private fun AddTaskOverlay(onSave: (String) -> Unit, onCancel: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    var text by remember { mutableStateOf("") }

    // Request focus and show keyboard when the overlay appears
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f))
                .clickable(onClick = onCancel)
        )

        Surface(
            tonalElevation = 3.dp,
            shape = RoundedCornerShape(
                topStart = 16.0.dp,
                topEnd = 16.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .imePadding() // pushes with IME
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholder = { Text("New task…") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { onSave(text) }
                    )
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onSave(text) }) {
                    Text("Save")
                }
            }
        }
    }
}
