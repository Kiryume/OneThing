package com.example.onething.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task")
    fun getAll(): Flow<List<Task>>

    // Snapshot list for non-reactive consumers (e.g., widgets)
    @Query("SELECT * FROM Task")
    suspend fun getAllOnce(): List<Task>

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task): Int
}