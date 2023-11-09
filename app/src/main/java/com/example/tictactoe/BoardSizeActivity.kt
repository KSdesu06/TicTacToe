package com.example.tictactoe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.tictactoe.databinding.ActivityBoardSizeBinding

class BoardSizeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardSizeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardSizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpDoneButton(binding.sizeEdt)
        setUpDoneButton(binding.playerName1Edt)
        setUpDoneButton(binding.playerName2Edt)

        binding.startButton.setOnClickListener {
            val size = binding.sizeEdt.text.toString().toIntOrNull() ?: 0
            val playerOne = binding.playerName1Edt.text.toString() ?: ""
            val playerTwo = binding.playerName2Edt.text.toString() ?: ""

            when {
                size !in 3..10 -> {
                    binding.sizeEdt.error = "Please enter a valid size (3 - 10)"
                }

                playerOne.isEmpty() -> {
                    binding.playerName1Edt.error = "Please enter a player X name"
                }

                playerTwo.isEmpty() -> {
                    binding.playerName2Edt.error = "Please enter a player O name"
                }

                playerOne.equals(playerTwo, ignoreCase = true) -> {
                    binding.playerName1Edt.error = "Player names must be different"
                    binding.playerName2Edt.error = "Player names must be different"
                }

                else -> {
                    val intent = Intent(this@BoardSizeActivity, MainActivity::class.java)
                    intent.putExtra("grid_size", size)
                    intent.putExtra("player_name_1", playerOne)
                    intent.putExtra("player_name_2", playerTwo)
                    startActivity(intent)
                }
            }
        }

        binding.historyBtn.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpDoneButton(editText: EditText) {
        editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else false
        }
    }
}