package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.TechNote
import kotlinx.coroutines.flow.Flow

@Dao
interface TechNoteDao {
    @Query("SELECT * FROM tech_notes WHERE projectId = :projectId ORDER BY updatedAt DESC")
    fun getByProject(projectId: Long): Flow<List<TechNote>>

    @Insert
    suspend fun insert(note: TechNote): Long

    @Update
    suspend fun update(note: TechNote)

    @Delete
    suspend fun delete(note: TechNote)

    @Query("DELETE FROM tech_notes WHERE projectId = :projectId")
    suspend fun deleteByProject(projectId: Long)

    @Query("SELECT * FROM tech_notes WHERE id = :id")
    suspend fun getById(id: Long): TechNote?
}
