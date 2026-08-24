package com.lifeapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dev_tasks")
data class DevTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val content: String,
    val status: Int = 0, // 0=pending, 1=in_progress, 2=completed
    val priority: Int = 1, // 0=high, 1=medium, 2=low
    val createdAt: Long = System.currentTimeMillis()
)
