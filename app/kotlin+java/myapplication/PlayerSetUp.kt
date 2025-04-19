package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PlayerSetUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.player_set_up)// Ielādē atbilstošo layoutu
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val EditText_player1 = findViewById<EditText>(R.id.editTextText)
        val EditText_player2 = findViewById<EditText>(R.id.editTextText2)
        val button = findViewById<Button>(R.id.button2)
        val button2 = findViewById<Button>(R.id.button5)
        button.setOnClickListener {
            val player1Name = EditText_player1.text.toString()
            val player2Name = EditText_player2.text.toString()

            // Notiek pārbaude, vai spēlētāju vārdi ir ievadīti, un sākas spēle
            if (player1Name.isNotEmpty() && player2Name.isNotEmpty()) {
                val intent = Intent(this, TikTakToe_Game::class.java)
                intent.putExtra("player1", player1Name)
                intent.putExtra("player2", player2Name)
                startActivity(intent)
            } else {
                // Ja kāds no laukiem ir tukšs, parādām brīdinājuma ziņojumu
                Toast.makeText(this, "Please enter names for both players", Toast.LENGTH_SHORT).show()
            }
        }
        // Atgriešanās poga uz Home ekranu
        button2.setOnClickListener {
            val intentButton2 = Intent(this, MainActivity::class.java)
            startActivity(intentButton2)
        }
    }
}