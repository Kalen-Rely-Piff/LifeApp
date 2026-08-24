package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.PublishPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface PublishPlanDao {
    @Query("SELECT * FROM publish_plans WHERE date = :date ORDER BY createdAt DESC")
    fun getByDate(date: String): Flow<List<PublishPlan>>

    @Query("SELECT * FROM publish_plans ORDER BY date ASC")
    fun getAll(): Flow<List<PublishPlan>>

    @Query("SELECT COUNT(*) FROM publish_plans WHERE date = :date AND isPublished = 0")
    suspend fun getPendingCount(date: String): Int

    @Insert
    suspend fun insert(plan: PublishPlan): Long

    @Update
    suspend fun update(plan: PublishPlan)

    @Delete
    suspend fun delete(plan: PublishPlan)
}
