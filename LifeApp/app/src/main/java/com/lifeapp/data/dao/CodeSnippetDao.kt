package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.CodeSnippet
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeSnippetDao {
    @Query("SELECT * FROM code_snippets WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getByProject(projectId: Long): Flow<List<CodeSnippet>>

    @Insert
    suspend fun insert(snippet: CodeSnippet): Long

    @Update
    suspend fun update(snippet: CodeSnippet)

    @Delete
    suspend fun delete(snippet: CodeSnippet)

    @Query("DELETE FROM code_snippets WHERE projectId = :projectId")
    suspend fun deleteByProject(projectId: Long)
}
