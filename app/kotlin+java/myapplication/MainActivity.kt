package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        Toast.makeText(this, "Welcome to Tik-Tak-Toe game!", Toast.LENGTH_SHORT).show()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val buttonFriend = findViewById<Button>(R.id.button)
        val buttonBot = findViewById<Button>(R.id.button3)
        val buttonAchievements = findViewById<Button>(R.id.button4)
        // Spēlet ar draugu button
        buttonFriend.setOnClickListener {
            val intent = Intent(this, PlayerSetUp::class.java)
            startActivity(intent)
        }
        // Spēlet ar botu button
        buttonBot.setOnClickListener {
            val intentBot = Intent(this, TikTakToe_Game::class.java)
            intentBot.putExtra("isBotGame", true)
            startActivity(intentBot)
        }
        // Achievement button
        buttonAchievements.setOnClickListener {
            val intentAchievements = Intent(this, AchievementsActivity::class.java)
            startActivity(intentAchievements)
        }
    }
}
