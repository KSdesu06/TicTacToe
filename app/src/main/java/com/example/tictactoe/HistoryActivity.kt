package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tictactoe.database.GameDatabase
import com.example.tictactoe.databinding.ActivityHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val histories = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, histories)
        binding.historyListView.adapter = adapter

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

        binding.backButton2.setOnClickListener() {
            finish()
        }

        binding.homeButton.setOnClickListener() {
            intent = Intent(this, BoardSizeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}
