package com.lifeapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class Material(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val type: Int = 0, // 0=image, 1=video, 2=other
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
