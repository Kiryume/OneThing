// Kotlin
package com.example.onething.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.onething.data.Task

@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onAddClicked: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClicked, modifier = Modifier.padding(0.dp, 0.dp, 8.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp),
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
                    .padding(8.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 4.dp,
                ) {
                    Column(
                        modifier = Modifier.defaultMinSize(minHeight = 100.dp)
                    ) {
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
