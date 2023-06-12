package com.example.tea1


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle

import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import android.view.Menu
import android.view.MenuItem
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var okButton: Button

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiManager: ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        okButton = findViewById(R.id.okButton)

        // Initialiser les SharedPreferences et l'API service
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Vérifier l'accès au réseau et activer le bouton OK si disponible
        if (isNetworkAvailable()) {
            okButton.isEnabled = true
        } else {
            Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show()
        }

        val deco = intent.getStringExtra("deco") ?: "1"

        if (deco.toInt() == 1) {
            val username = sharedPreferences.getString("username", "")
            val password = sharedPreferences.getString("password", "")
            authenticateUser(username ?: "", password ?: "")
        }

        okButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Enregistrer username et password dans les préférences :

            val editor: SharedPreferences.Editor = sharedPreferences.edit()

            editor.putString("username", username)
            editor.putString("password", password)
            editor.apply()

            // Vérifier si les champs sont vides
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            } else {
                // Demander une identification auprès de l'API
                authenticateUser(username, password)
            }
        }


    }
    private fun authenticateUser(username: String, password: String) {

        val requestQueue = Volley.newRequestQueue(this)
        apiManager = ApiManager(this, sharedPreferences)

        val url = "http://tomnab.fr/todo-api/authenticate?user=$username&password=$password"
        val request = apiManager.makeLoginRequest(url)
        requestQueue.add(request)
        }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_preferences -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}





