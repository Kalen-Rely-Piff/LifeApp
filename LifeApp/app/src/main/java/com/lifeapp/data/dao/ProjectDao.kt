package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Project>>

    @Insert
    suspend fun insert(project: Project): Long

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getById(id: Long): Project?
}
