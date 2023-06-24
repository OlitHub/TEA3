package com.example.tea1

import DatabaseHelper
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


class ShowListActivity : AppCompatActivity() {

    private lateinit var apiManager: ApiManager
    private lateinit var dbHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_show_list)

        // Récupérer les données passées depuis ChoixListActivity (id de la liste cliquée)
        val idList = intent.getStringExtra("id")?.toInt()

        // Passer l'id de la liste actuelle dans les préférences :
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("listId", idList.toString())
        editor.apply()

        val username = sharedPreferences.getString("username", "")

        // Initialisation du recyclerView, on le mettra à jour avec les items de la liste par la suite

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView2)
        val adapter = ItemAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // On met à jour le recyclerView avec les items de la liste actuelle

        dbHelper = DatabaseHelper(this)
        apiManager = ApiManager(this, sharedPreferences)
        adapter.updateList(dbHelper.getTodosByListId(idList ?: 0))

        val butNouvellItem: Button = findViewById(R.id.buttonNouvelItem)
        val editTextNouvellItem: EditText = findViewById(R.id.editTextNouvelItem)

        butNouvellItem.setOnClickListener {
            // On envoie une requête pour afficher l'item puis on envoie une requête pour mettre à jour le recyclerView
            val nouvelItemName = editTextNouvellItem.text.toString()
            apiManager.addListItemRequest(nouvelItemName, idList)
            apiManager.updateDatabaseRequest(username ?: "tom")

            adapter.updateList(dbHelper.getTodosByListId(idList ?: 0))

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
