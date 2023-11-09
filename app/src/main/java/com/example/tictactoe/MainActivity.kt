package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tictactoe.database.GameDatabase
import com.example.tictactoe.database.History
import com.example.tictactoe.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var buttons: Array<Array<Button?>>
    private lateinit var xNameFromEdt: String
    private lateinit var oNameFromEdt: String
    private var currentPlayer = "X"
    private var playerXScore = 0
    private var playerOScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the names and size from the intent
        xNameFromEdt = intent.getStringExtra("player_name_1") ?: "Player X"
        oNameFromEdt = intent.getStringExtra("player_name_2") ?: "Player O"
        val size = intent.getIntExtra("grid_size", 3)

        // Set the TextViews with the names of the players received from the BoardSizeActivity
        binding.playerXName.text = xNameFromEdt
        binding.playerOName.text = oNameFromEdt

        // Update the TextView to indicate which player will start the game
        binding.playerTurn.text = "Player $currentPlayer to start"

        startGame(size)
        setUpListeners()
    }

    // set Button
    private fun setUpListeners() {
        // set reset button
        binding.resetButton.setOnClickListener() {
            resetGame()
            startGame(intent.getIntExtra("grid_size", 3))
        }

        // set back button
        binding.backButton.setOnClickListener {
            intent = Intent(this, BoardSizeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        // set history button
        binding.historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun resetGame() {
        currentPlayer = "X"
        binding.playerTurn.text = "Player $currentPlayer to start"
        binding.gridLayout.removeAllViews()
    }

    private fun startGame(size: Int) {
        binding.gridLayout.post {
            val gridWidth =
                binding.gridLayout.measuredWidth - binding.gridLayout.paddingStart - binding.gridLayout.paddingEnd
            val buttonSize = gridWidth / size

            binding.gridLayout.removeAllViews()
            binding.gridLayout.columnCount = size
            binding.gridLayout.rowCount = size
            buttons = Array(size) { arrayOfNulls(size) }

            for (i in 0 until size) {
                for (j in 0 until size) {
                    val button = Button(this)
                    when {
                        size < 7 -> button.textSize = 30f
                        size < 10 -> button.textSize = 15f
                        else -> button.textSize = 9f
                    }
                    button.setOnClickListener {
                        onGridButtonClick(it as Button, i, j)
                    }
                    buttons[i][j] = button
                    binding.gridLayout.addView(button)

                    val params = GridLayout.LayoutParams()
                    params.width = buttonSize
                    params.height = buttonSize
                    params.setMargins(0, 0, 0, 0)

                    button.layoutParams = params
                }
            }
        }
    }

    private fun onGridButtonClick(button: Button, i: Int, j: Int) {

        if (button.text == "") {
            button.text = currentPlayer

            if (checkWinner(i, j)) {
                val winnerName = if (currentPlayer == "X") xNameFromEdt else oNameFromEdt
                binding.playerTurn.text = "Player $winnerName won!"
                if (currentPlayer == "X") playerXScore++ else playerOScore++
                updateScore()
                saveGameHistory(winnerName, buttons.size, xNameFromEdt, oNameFromEdt)
                disabledButton()
            } else {
                // Check if all cells are filled and it's a draw
                if (isGameDraw()) {
                    binding.playerTurn.text = "Game is a draw"
                    saveGameHistory("No one won", buttons.size, xNameFromEdt, oNameFromEdt)
                    disabledButton()
                } else {
                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                    binding.playerTurn.text = "Player $currentPlayer's turn"
                }
            }

        }
    }

    private fun checkWinner(i: Int, j: Int): Boolean {

        // Check row
        if (buttons[i].all { it?.text == currentPlayer }) {
            return true
        }

        // Check column
        if (buttons.all { it[j]?.text == currentPlayer }) {
            return true
        }

        // Check diagonal top-left to bottom-right
        if (i == j && buttons.indices.all { index -> buttons[index][index]?.text == currentPlayer }) {
            return true
        }

        // Check diagonal top-right to bottom-left
        if (i + j == buttons.size - 1 && buttons.indices.all { index ->
                buttons[index][buttons.size - 1 - index]?.text == currentPlayer
            }) {
            return true
        }
        return false
    }

    private fun isGameDraw(): Boolean {
        // This function checks if the game is a draw
        return buttons.all { row -> row.all { it?.text?.isNotEmpty() == true } }
    }

    private fun updateScore() {
        val playerXScoreTxt: TextView = findViewById(R.id.player_x_score)
        val playerOScoreTxt: TextView = findViewById(R.id.player_o_score)

        playerXScoreTxt.text = "score X : $playerXScore"
        playerOScoreTxt.text = "score O : $playerOScore"

    }

    private fun disabledButton() {
        for (row in buttons) {
            for (button in row) {
                button?.isEnabled = false
            }
        }
    }

    private fun saveGameHistory(
        winner: String,
        boardSize: Int,
        playerXName: String,
        playerOName: String
    ) {
        val history = History(
            boardSizeHis = boardSize,
            playerWins = winner,
            playerXName = playerXName,
            playerOName = playerOName,
        )
        val db = GameDatabase.getDatabase(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            db.historyDao().insert(history)
        }
    }
}
