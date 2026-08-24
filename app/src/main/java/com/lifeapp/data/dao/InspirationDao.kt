package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.Inspiration
import kotlinx.coroutines.flow.Flow

@Dao
interface InspirationDao {
    @Query("SELECT * FROM inspirations ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Inspiration>>

    @Insert
    suspend fun insert(inspiration: Inspiration): Long

    @Update
    suspend fun update(inspiration: Inspiration)

    @Delete
    suspend fun delete(inspiration: Inspiration)
}
