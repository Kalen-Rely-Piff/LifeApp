package com.lifeapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val date: String, // yyyy-MM-dd
    val priority: Int = 1, // 0=high, 1=medium, 2=low
    val reminderTime: Long? = null, // timestamp in millis
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
