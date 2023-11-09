package com.example.tictactoe.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class History(
    @NonNull @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerXName: String,
    val playerOName: String,
    val playerWins: String,
    val boardSizeHis: Int
)