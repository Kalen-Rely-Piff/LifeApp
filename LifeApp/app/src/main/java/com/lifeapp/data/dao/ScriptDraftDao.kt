package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.ScriptDraft
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDraftDao {
    @Query("SELECT * FROM script_drafts ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<ScriptDraft>>

    @Query("SELECT COUNT(*) FROM script_drafts")
    suspend fun getCount(): Int

    @Insert
    suspend fun insert(draft: ScriptDraft): Long

    @Update
    suspend fun update(draft: ScriptDraft)

    @Delete
    suspend fun delete(draft: ScriptDraft)

    @Query("SELECT * FROM script_drafts WHERE id = :id")
    suspend fun getById(id: Long): ScriptDraft?
}
