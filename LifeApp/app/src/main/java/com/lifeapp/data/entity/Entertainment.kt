package com.lifeapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entertainments")
data class Entertainment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: Int = 0, // 0=movie, 1=tv, 2=book, 3=game
    val status: Int = 0, // 0=want, 1=watching, 2=watched
    val rating: Int = 0, // 0-5
    val thoughts: String = "",
    val progress: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
