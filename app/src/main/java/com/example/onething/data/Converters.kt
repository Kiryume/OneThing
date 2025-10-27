package com.example.onething.data

import androidx.room.TypeConverter
import java.time.Instant

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    @JvmStatic
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }
}
