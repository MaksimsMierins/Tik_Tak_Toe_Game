package com.example.myapplication

import android.content.Context
import android.widget.Toast

object Achievements {
    // Izmantojam atsevišķu SharedPreferences nosaukumu, lai neparklātos ar citiem datiem
    private const val PREF_NAME = "achievements_prefs"

    // Atbloķē "Achievement", tikai ja tas vēl nav atverts
    fun unlock(context: Context, key: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        // Lai izvairītos no Achievement atkārtotas atbloķēšanas
        if (!prefs.getBoolean(key, false)) {
            // Mainam statusu uz atbloķēts
            prefs.edit().putBoolean(key, true).apply()
            val achievementName = when (key) {
                //paziņojumi lietotājam
                "achievement_play" -> context.getString(R.string.Achievement1)
                "achievement_bot_play" -> context.getString(R.string.Achievement2)
                "achievement_friend" -> context.getString(R.string.Achievement3)
                "achievement_win_bot" -> context.getString(R.string.Achievement4)
                "achievement_lose_bot" -> context.getString(R.string.Achievement5)
                "achievement_all" -> context.getString(R.string.Achievement6)
                else -> "Achievement unlocked!"
            }
            Toast.makeText(context, "Achievement unlocked: $achievementName", Toast.LENGTH_SHORT).show()
            checkAllAchievements(context)
        }
    }
    // Pārbauda vai konkrēts Achievement jau ir atbloķēts
    fun isUnlocked(context: Context, key: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, false)
    }
    // Pārbaudām, vai ir atbloķēti visi  Achievement, lai varetu pēc tam atbloķet  Achievement "All Achievements are unlocked!"
    private fun checkAllAchievements(context: Context) {
        val required = listOf(
            "achievement_play",
            "achievement_bot_play",
            "achievement_friend",
            "achievement_win_bot",
            "achievement_lose_bot"
        )
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val unlocked = required.all { prefs.getBoolean(it, false) }
        // Atbloķe Achievement "All Achievements are unlocked!"
        if (unlocked && !prefs.getBoolean("achievement_all", false)) {
            unlock(context, "achievement_all")
        }
    }
    // Atjauno sasniegumu statusus uz “neatbloķēts”, funkcija vajadzīga lai varētu pilnīgi Reset Achievements(Parbaldes deļ)
    fun resetAchievements(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("achievement_play", false)
        editor.putBoolean("achievement_bot_play", false)
        editor.putBoolean("achievement_friend", false)
        editor.putBoolean("achievement_win_bot", false)
        editor.putBoolean("achievement_lose_bot", false)
        editor.putBoolean("achievement_all", false)
        editor.apply()
        // Indikators, ka Achievements are reset
        Toast.makeText(context, "Achievements have been reset!", Toast.LENGTH_SHORT).show()
    }
}
