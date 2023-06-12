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
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class ChoixListActivity : AppCompatActivity() {

    private lateinit var pseudo: String
    private lateinit var apiManager: ApiManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_choix_list)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Afficher la liste des listes de l'utilisateur
        val requestQueue = Volley.newRequestQueue(this)
        apiManager = ApiManager(this, sharedPreferences)

        val requestUserLists = apiManager.userListsRequest()
        requestQueue.add(requestUserLists)

        val userListString: String? = sharedPreferences.getString("userLists", "") ?:""

        val gson = Gson()
        val listListJson = gson.fromJson(userListString, Array<ListListJson>::class.java)
        val listList = mutableListOf<ListList>()

        listListJson.forEachIndexed { index, listListJson ->
            val listItem = ListList(listListJson.id.toInt(), listListJson.name)
            listList.add(listItem)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ListListAdapter(listList)
        recyclerView.adapter = adapter

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
            val nouvelleListeName = editTextNouvelleListe.text.toString()
            val request = apiManager.addListRequest(nouvelleListeName)
            requestQueue.add(request)
            recreate()
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

    data class ListListJson(
        @SerializedName("id") val id: String,
        @SerializedName("label") val name: String?
    )
}
