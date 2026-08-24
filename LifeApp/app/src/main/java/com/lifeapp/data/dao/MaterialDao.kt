package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.Material
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials ORDER BY isCompleted ASC, createdAt DESC")
    fun getAll(): Flow<List<Material>>

    @Insert
    suspend fun insert(material: Material): Long

    @Update
    suspend fun update(material: Material)

    @Delete
    suspend fun delete(material: Material)
}
