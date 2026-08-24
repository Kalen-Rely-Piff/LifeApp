package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos WHERE date = :date ORDER BY isCompleted ASC, priority ASC, createdAt DESC")
    fun getTodosByDate(date: String): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE date = :date AND isCompleted = 0 ORDER BY priority ASC, createdAt DESC LIMIT :limit")
    fun getPendingTodosByDate(date: String, limit: Int): Flow<List<Todo>>

    @Query("SELECT COUNT(*) FROM todos WHERE date = :date AND isCompleted = 0")
    suspend fun getPendingCount(date: String): Int

    @Query("SELECT COUNT(*) FROM todos WHERE date = :date")
    suspend fun getTotalCount(date: String): Int

    @Insert
    suspend fun insert(todo: Todo): Long

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("DELETE FROM todos WHERE date = :date AND isCompleted = 1")
    suspend fun deleteCompleted(date: String)

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getById(id: Long): Todo?
}
