package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.tictactoe.database.GameDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history)

        val historyListView: ListView = findViewById(R.id.historyListView)
        // val deleteBtn: Button = findViewById(R.id.delete_button)
        val histories = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, histories)
        historyListView.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            val gameHistories =
                GameDatabase.getDatabase(applicationContext).historyDao().getAllHistories()
            withContext(Dispatchers.Main) {
                gameHistories.forEach { history ->
                    histories.add("Round: ${history.id} \n" +
                            "\tBoard Size: ${history.boardSizeHis} x ${history.boardSizeHis} \n" +
                            "\tWinner: ${history.playerWins} \n" +
                            "\tPlayer X Name: ${history.playerXName} \n" +
                            "\tPlayer O Name: ${history.playerOName}\n"
                    )
                }
                adapter.notifyDataSetChanged()
            }
        }

        val backBtn: Button = findViewById(R.id.back_button2)
        val homeBtn: Button = findViewById(R.id.home_button)

        backBtn.setOnClickListener() {
            finish()
        }

        homeBtn.setOnClickListener() {
            val intent = Intent(this, BoardSizeActivity::class.java)
            startActivity(intent)
        }
    }
}
