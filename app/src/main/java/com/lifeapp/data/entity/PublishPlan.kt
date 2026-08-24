package com.lifeapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "publish_plans")
data class PublishPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // yyyy-MM-dd
    val draftId: Long? = null,
    val note: String = "",
    val isPublished: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
