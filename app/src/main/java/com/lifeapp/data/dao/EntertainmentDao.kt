package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.Entertainment
import kotlinx.coroutines.flow.Flow

@Dao
interface EntertainmentDao {
    @Query("SELECT * FROM entertainments ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Entertainment>>

    @Query("SELECT * FROM entertainments WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: Int): Flow<List<Entertainment>>

    @Query("SELECT * FROM entertainments WHERE type = :type AND status = 0 ORDER BY createdAt DESC")
    fun getWantToWatchByType(type: Int): Flow<List<Entertainment>>

    @Query("SELECT * FROM entertainments WHERE status = 0 ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWant(): Entertainment?

    @Query("SELECT COUNT(*) FROM entertainments WHERE status = 0")
    suspend fun getWantCount(): Int

    @Insert
    suspend fun insert(item: Entertainment): Long

    @Update
    suspend fun update(item: Entertainment)

    @Delete
    suspend fun delete(item: Entertainment)
}
