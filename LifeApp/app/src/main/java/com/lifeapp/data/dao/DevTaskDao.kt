package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.DevTask
import kotlinx.coroutines.flow.Flow

@Dao
interface DevTaskDao {
    @Query("SELECT * FROM dev_tasks WHERE projectId = :projectId ORDER BY status ASC, priority ASC, createdAt DESC")
    fun getByProject(projectId: Long): Flow<List<DevTask>>

    @Query("SELECT COUNT(*) FROM dev_tasks WHERE projectId = :projectId AND status != 2")
    suspend fun getPendingCount(projectId: Long): Int

    @Query("SELECT COUNT(*) FROM dev_tasks WHERE projectId = :projectId")
    suspend fun getTotalCount(projectId: Long): Int

    @Query("SELECT COUNT(*) FROM dev_tasks WHERE projectId = :projectId AND status = 2")
    suspend fun getCompletedCount(projectId: Long): Int

    @Insert
    suspend fun insert(task: DevTask): Long

    @Update
    suspend fun update(task: DevTask)

    @Delete
    suspend fun delete(task: DevTask)

    @Query("DELETE FROM dev_tasks WHERE projectId = :projectId")
    suspend fun deleteByProject(projectId: Long)
}
