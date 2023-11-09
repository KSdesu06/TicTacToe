package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tictactoe.database.GameDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val historyListView: ListView = findViewById(R.id.historyListView)
        val histories = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, histories)
        historyListView.adapter = adapter

        lifecycleScope.launch {
            val gameHistories = withContext(Dispatchers.IO) {
                GameDatabase.getDatabase(applicationContext).historyDao().getAllHistories()
            }
            if (gameHistories.isEmpty()) {
                histories.add("No history records found.")
            } else {
                gameHistories.forEach { history ->
                    histories.add("\nGame: ${history.id}\n" +
                            "\t\tBoard Size: ${history.boardSizeHis} x ${history.boardSizeHis} \n" +
                            "\t\tPlayer X Name: ${history.playerXName}\n" +
                            "\t\tPlayer O Name: ${history.playerOName}\n" +
                            "\t\tWinner: ${history.playerWins}\n"
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
            intent = Intent(this, BoardSizeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}
