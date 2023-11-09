package com.example.tictactoe.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {

    @Insert
    suspend fun insert(history: History)

    @Delete
    suspend fun delete(history: History)

    @Query("SELECT * FROM history_table ORDER BY id ASC")
    suspend fun getAllHistories(): List<History>

}