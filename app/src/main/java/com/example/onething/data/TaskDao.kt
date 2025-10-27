package com.example.onething.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getAllToday(start: Instant = DateUtils.todayRange().first, end: Instant = DateUtils.todayRange().second): Flow<List<Task>>

    // Snapshot list for non-reactive consumers (e.g., widgets)
    @Query("SELECT * FROM Task")
    suspend fun getAllOnce(): List<Task>

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task): Int
}

object DateUtils {
    fun todayRange(): Pair<Instant, Instant> {
        val now = java.time.ZonedDateTime.now()
        val startZdt = now.toLocalDate().atStartOfDay(now.zone)
        val endZdt = startZdt.plusDays(1).minusNanos(1)
        return startZdt.toInstant() to endZdt.toInstant()
    }
}
