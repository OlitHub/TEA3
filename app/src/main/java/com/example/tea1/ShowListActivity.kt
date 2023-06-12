package com.example.tea1

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
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


class ShowListActivity : AppCompatActivity() {

    private lateinit var apiManager: ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        // Récupérer les données passées depuis ChoixListActivity
        val idList = intent.getStringExtra("id")?.toInt()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView2)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // Passer l'id de la liste actuelle dans les préférences :

        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("listId", idList.toString())
        editor.apply()

        val requestQueue = Volley.newRequestQueue(this)
        apiManager = ApiManager(this, sharedPreferences)

        val requestGetItemsList = idList?.let { apiManager.getItemsListRequest(it) }
        requestQueue.add(requestGetItemsList)

        // Récupérer les items

        val itemListString: String? = sharedPreferences.getString("itemList", "") ?:""

        val itemListJson = gson.fromJson(itemListString, Array<itemListJSON>::class.java)
        val itemList = mutableListOf<item>()

        itemListJson.forEachIndexed { index, itemListJson ->
            val listItem = item(itemListJson.id.toInt(), itemListJson.name, itemListJson.url, itemListJson.checked)
            itemList.add(listItem)
        }


        val adapter = ItemAdapter(itemList, idList)
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)


        val butNouvellItem: Button = findViewById(R.id.buttonNouvelItem)
        val editTextNouvellItem: EditText = findViewById(R.id.editTextNouvelItem)

        butNouvellItem.setOnClickListener {
            val nouvelItemName = editTextNouvellItem.text.toString()
            val addItemRequest = apiManager.addListItemRequest(nouvelItemName, idList)
            requestQueue.add(addItemRequest)
            recreate() // On attend un peu le temps que l'objet soit ajouté dans la base de données de l'API
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

    data class itemListJSON(
        @SerializedName("id") val id: String,
        @SerializedName("label") val name: String?,
        @SerializedName("url") val url: String,
        @SerializedName("checked") val checked: Boolean
    )

}
