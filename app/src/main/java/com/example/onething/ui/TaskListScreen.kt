// Kotlin
package com.example.onething.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.onething.data.Task

@Composable
fun TaskListScreen(tasks: List<Task>, onAddTask: (String) -> Unit) {
    var newTask by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onAddTask(newTask)
                newTask = ""
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(0.dp, 16.dp, ),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Tasks",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Thin
                )
            }
            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                OutlinedTextField(
                    value = newTask,
                    onValueChange = { newTask = it },
                    label = { Text("New task") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Surface(
                    // rounded corners and a slight elevation for the task list
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 4.dp,

                    ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 8.dp)
                        ) { Text("My Tasks", fontWeight = FontWeight.Bold) }
                        LazyColumn {
                            items(tasks.size) { i ->
                                ListItem(
                                    headlineContent = { Text(tasks[i].name) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
