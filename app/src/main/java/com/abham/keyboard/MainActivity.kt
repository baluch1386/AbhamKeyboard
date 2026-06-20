package com.abham.keyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.abham.keyboard.databinding.ActivityMainBinding

/**
 * MainActivity - The screen the user sees when they open "کیبورد ابهام" from the app drawer.
 * It shows the app title and a button that takes the user straight to the system
 * keyboard settings, where they can enable and select this keyboard.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.openSettingsButton.setOnClickListener {
            // Opens the system "Enable keyboards" settings screen
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            startActivity(intent)
        }
    }
}
