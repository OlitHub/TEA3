package com.example.tea1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var baseUrlEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var disconnectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        baseUrlEditText = findViewById(R.id.editTextBaseUrl)
        saveButton = findViewById(R.id.buttonSave)
        disconnectButton = findViewById(R.id.disconnectButton)

        // Récupérer l'URL de base actuelle depuis les préférences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentBaseUrl = sharedPreferences.getString("base_url", "")
        baseUrlEditText.setText(currentBaseUrl)

        saveButton.setOnClickListener {
            val newBaseUrl = baseUrlEditText.text.toString()

            // Sauvegarder la nouvelle URL de base dans les préférences
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("base_url", newBaseUrl)
            editor.apply()

            finish()
        }

        disconnectButton.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            intent.putExtra("deco", "0")
            startActivity(intent)
        }
    }
}

