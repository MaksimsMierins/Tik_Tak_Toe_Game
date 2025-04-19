package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AchievementsActivity : AppCompatActivity() {
    //Teksta lauki katram sasniegumam
    private lateinit var achievementTitles: List<TextView>
    // Atslēgas, kas saistītas ar katru Achievement SharedPreferences glabātuvē
    private val achievementKeys = listOf(
        "achievement_play",
        "achievement_bot_play",
        "achievement_friend",
        "achievement_win_bot",
        "achievement_lose_bot",
        "achievement_all")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievements)
        // Piesaistām TextView elementus no UI, lai vēlāk varētu mainīt to krāsu
        achievementTitles = listOf(
            findViewById(R.id.achievementTitle1),
            findViewById(R.id.achievementTitle2),
            findViewById(R.id.achievementTitle3),
            findViewById(R.id.achievementTitle4),
            findViewById(R.id.achievementTitle5),
            findViewById(R.id.achievementTitle6))
        // Uz UI tas ir "Home" button, vienkarši lai varetu pariet starp aktevitatēm
        val backButton: Button = findViewById(R.id.button5)
        backButton.setOnClickListener {
            finish()
        }
        // Uz UI tas ir "Reset Button", lai atjaunotu Achievement statusu uz “neatbloķēts”
        val resetButton: Button = findViewById(R.id.button8)
        resetButton.setOnClickListener {
            // Atiestata SharedPreferences vērtības
            Achievements.resetAchievements(this)
            //Pēc atiestatīšanas vizuāli samaina krāsu uz pelēku
            updateAchievementUI()
        }
        // Kad aktivitāte tiek izveidota, parāda Achievement statusus
        updateAchievementUI()
    }
    // Kad lietotājs atgriežas uz šo ekrānu, mēs atjaunojam UI un lai nebutu kazusus, gadījumam, ja kaut kas mainījās citas aktevitātes
    override fun onResume() {
        super.onResume()
        updateAchievementUI()
    }
    // Funkcija, kas pārkrāso sasniegumu tekstus atbilstoši to statusam (zaļš = izpildīts, pelēks = nav izpildīts)
    private fun updateAchievementUI() {
        for (i in achievementTitles.indices) {
            val isUnlocked = Achievements.isUnlocked(this, achievementKeys[i])
            achievementTitles[i].setTextColor(
                if (isUnlocked) Color.GREEN else Color.GRAY
            )
        }
    }
}
