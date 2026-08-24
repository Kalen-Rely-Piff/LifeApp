package com.lifeapp.data.dao

import androidx.room.*
import com.lifeapp.data.entity.Memo
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Query("SELECT * FROM memos ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Memo>>

    @Insert
    suspend fun insert(memo: Memo): Long

    @Delete
    suspend fun delete(memo: Memo)

    @Update
    suspend fun update(memo: Memo)
}
