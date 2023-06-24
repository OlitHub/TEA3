package com.example.tea1

import DatabaseHelper
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ApiManager(private val context: Context, private val preferences: SharedPreferences) {

    val baseurl = preferences.getString("base_url", "http://tomnab.fr/todo-api/")
    private lateinit var dbHelper: DatabaseHelper
    fun makeLoginRequest(url: String) {

        val request = JsonObjectRequest(
            Request.Method.POST, url, null, { response ->
                val token = response["hash"]
                println("Token: $token")
                Log.i("test1", token.toString())

                if (token != null) {

                    // On récupère le token dans les préférences
                    val editor: SharedPreferences.Editor = preferences.edit()
                    editor.putString("token", token as String?)
                    editor.apply()

                    // On passe à la prochaine activité (ChoixListActivity)
                    val intent = Intent(context, ChoixListActivity::class.java)
                    context.startActivity(intent)
                    (context as MainActivity).finish()
                } else {
                    Toast.makeText(context, "Pas de token", Toast.LENGTH_LONG).show()

                }

            }, { _ ->
                Toast.makeText(context, "Erreur lors de la connexion", Toast.LENGTH_LONG).show()

            })

        Volley.newRequestQueue(context).add(request)

    }

    fun addListRequest(list_name: String) {

        val url = baseurl + "lists?label=$list_name"

        val request = object: JsonObjectRequest(
            Method.POST, url, null, { _ ->
                Toast.makeText(context, "Liste créée", Toast.LENGTH_LONG).show()
                                    }, { _ ->
                Toast.makeText(context, "Erreur lors de la création de la liste", Toast.LENGTH_LONG).show()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)

    }

    fun userListsRequest() {
        val url = baseurl + "lists"
        dbHelper = DatabaseHelper(context)

        val request = object: JsonObjectRequest( Method.GET, url, null,
            { response ->
                val responseList = response.getJSONArray("lists")
                val jsonString = responseList.toString()

                val gson = Gson()
                val userLists: List<item> = gson.fromJson(jsonString, object : TypeToken<List<item>>() {}.type)
                val username = preferences.getString("username", "")

                val listList = mutableListOf<ListList>()

                userLists.forEachIndexed { _, userLists ->
                    val listItem = ListList(userLists.id, userLists.label)
                    dbHelper.insertList(username ?: "vlad", userLists.id, userLists.label)
                    listList.add(listItem)
                }

                saveListToPreferences(userLists)


            }, { _ ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)

    }
    fun saveListToPreferences(list: List<item>) {
        val editor: SharedPreferences.Editor = preferences.edit()

        val gson = Gson()
        val json = gson.toJson(list)

        editor.putString("userLists", json)
        editor.apply()
    }

    fun getItemsListRequest(listId: Int) {
        val url = baseurl + "lists/$listId/items"
        dbHelper = DatabaseHelper(context)

        val request = object: JsonObjectRequest(
            Method.GET, url, null, { response ->

                val responseItems = response.getJSONArray("items")
                val jsonString = responseItems.toString()

                val gson = Gson()
                val itemList: MutableList<item> = gson.fromJson(jsonString, object : TypeToken<List<item>>() {}.type)

                saveItemListToPreferences(itemList)

                for (item in itemList) {
                    dbHelper.insertTodo(listId, item.id, item.label ?: "", item.url, item.checked)
                }


            }, { _ ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)

    }

    fun saveItemListToPreferences(list: List<item>) {
        val editor: SharedPreferences.Editor = preferences.edit()

        val gson = Gson()
        val json = gson.toJson(list)

        editor.putString("itemList", json)
        editor.apply()
    }

    fun addListItemRequest(item_name: String, idList: Int?) {

        val url = baseurl + "lists/${idList.toString()}/items?label=$item_name"

        val request = object: JsonObjectRequest(
            Method.POST, url, null, { _ ->
                Toast.makeText(context, "Item créé", Toast.LENGTH_LONG).show()


            }, { _ ->
                Toast.makeText(context, "Erreur lors de la création de l'item", Toast.LENGTH_LONG).show()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)

    }

    fun updateDatabaseRequest(username: String) {
            val url = baseurl + "lists"
            dbHelper = DatabaseHelper(context)

            val request = object: JsonObjectRequest( Method.GET, url, null,
                { response ->
                    val responseList = response.getJSONArray("lists")
                    val jsonString = responseList.toString()

                    val gson = Gson()
                    val userLists: List<item> = gson.fromJson(jsonString, object : TypeToken<List<item>>() {}.type)

                    userLists.forEachIndexed { _, userLists ->
                        dbHelper.insertList(username, userLists.id, userLists.label)
                        getItemsListRequest(userLists.id)
                    }

                    Toast.makeText(context, "Base de donnée à jour", Toast.LENGTH_LONG).show()

                }, { _ ->
                    Toast.makeText(context, "Erreur lors de la mise à jour de la base de données", Toast.LENGTH_LONG).show()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["hash"] = preferences.getString("token", "")!!
                    return headers
                }
            }

            Volley.newRequestQueue(context).add(request)
    }

    fun checkItemRequest(idItem: String, idList: Int?) {

        val url = baseurl + "lists/$idList/items/$idItem?check=1"

        val request = object: JsonObjectRequest(
            Method.PUT, url, null, { _ ->
                Toast.makeText(context, "Item checké", Toast.LENGTH_LONG).show()

            }, { _ ->
                Toast.makeText(context, "Erreur lors du check de l'item", Toast.LENGTH_LONG).show()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)

    }

    fun unCheckItemRequest(idItem: String, idList: Int?) {

        val url = baseurl + "lists/$idList/items/$idItem?check=0"

        val request = object: JsonObjectRequest(
            Method.PUT, url, null, { _ ->
                Toast.makeText(context, "Item checké", Toast.LENGTH_LONG).show()

            }, { _ ->
                Toast.makeText(context, "Erreur lors du check de l'item", Toast.LENGTH_LONG).show()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        Volley.newRequestQueue(context).add(request)

    }

}

