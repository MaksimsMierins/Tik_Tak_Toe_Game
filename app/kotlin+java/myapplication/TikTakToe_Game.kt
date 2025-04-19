package com.example.myapplication

import kotlin.random.Random
import android.content.Intent
import android.widget.TextView
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TikTakToe_Game : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tik_tak_toe__game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Pārbaudām, vai spēle ir pret botu, jo no tā ir atkarīgs, vai bots veiks gājienus vai nē
        val isBotGame = intent.getBooleanExtra("isBotGame", false)
        val boardView = findViewById<Tik_Tak_Toe_Board>(R.id.tikTakToeBoard)
        boardView.setGameMode(isBotGame)

        val winnerText = findViewById<TextView>(R.id.textView7)
        boardView.setWinnerTextView(winnerText, isBotGame)
        val currentPlayerText = findViewById<TextView>(R.id.textView4)
        boardView.setCurrentPlayerTextView(currentPlayerText)

        val button_Play_Again = findViewById<Button>(R.id.button11)
        val button_Home = findViewById<Button>(R.id.button12)

        val player1 = intent.getStringExtra("player1")
        val player2 = intent.getStringExtra("player2")
        boardView.setPlayerNames(player1, player2)

        // Šeit programma randomi izvēlas, kurš gājējs būs pirmais
        val firstPlayer = if (Random.nextBoolean()) "X" else "O"
        currentPlayerText.text = "Current Player: $firstPlayer"
        boardView.setCurrentPlayer(firstPlayer)
        // Ja spēle ir ar botu un pirmais ir bots (O), bots izdara gājienu
        if (isBotGame && firstPlayer == "O") {
            boardView.invalidate()
            boardView.postDelayed({ boardView.botMove() }, 500)// dilej uz 0.5 sec
        }
        // Button uz Home ekranu
        button_Home.setOnClickListener {
            val intent_Home = Intent(this, MainActivity::class.java)
            startActivity(intent_Home)
        }
        // Spelet vel reiz button
        button_Play_Again.setOnClickListener {
            boardView.resetGame()
            val firstPlayerAgain = if (Random.nextBoolean()) "X" else "O"
            currentPlayerText.text = "Current Player: $firstPlayerAgain"
            boardView.setCurrentPlayer(firstPlayerAgain)
            // Tads pats trigeris botam, lai viņš izpildīja gaenu kad ir pirmais, pēc spēles atjaunošdanas
            if (isBotGame && firstPlayerAgain == "O") {
                boardView.invalidate()
                boardView.postDelayed({ boardView.botMove() }, 500)
            }
        }
    }
}
