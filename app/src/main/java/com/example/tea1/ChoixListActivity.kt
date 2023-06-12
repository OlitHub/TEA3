package com.example.tea1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChoixListActivity : AppCompatActivity() {

    private lateinit var apiManager: ApiManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_choix_list)

        // On initialise le RecyclerView avec une liste vide qu'on va changer pour la liste des listes de l'utilisateur une fois que la requête aura abouti

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ListListAdapter(mutableListOf())
        recyclerView.adapter = adapter

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Récupérer et afficher la liste des listes de l'utilisateur

        apiManager = ApiManager(this, sharedPreferences)
        apiManager.userListsRequest { userLists ->
            adapter.updateList(userLists)
        }

        adapter.setOnItemClickListener(object :
            ListListAdapter.OnItemClickListener {
            override fun onItemClick(listItem: ListList?) {
                // Lorsque l'élément de la liste est cliqué, ouvrir la vue ShowListActivity
                val intent = Intent(this@ChoixListActivity, ShowListActivity::class.java)
                intent.putExtra("id", listItem?.id.toString())
                Log.i("idl", listItem?.id.toString())
                startActivity(intent)
            }
        })

        // Créer une nouvelle liste pour l'utilisateur connecté

        val butNouvelleListe: Button = findViewById(R.id.buttonNouvelleListe)
        val editTextNouvelleListe: EditText = findViewById(R.id.editTextNouvelleListe)

        butNouvelleListe.setOnClickListener {
            // On ajoute une liste et on met à jour le RecyclerView
            val nouvelleListeName = editTextNouvelleListe.text.toString()
            apiManager.addListRequest(nouvelleListeName)

            apiManager.userListsRequest { userLists ->
                adapter.updateList(userLists)
            }
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
