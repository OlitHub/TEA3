package com.example.tea1

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

    fun userListsRequest(callback: (List<ListList>) -> Unit) {
        val url = baseurl + "lists"

        val request = object: JsonObjectRequest( Method.GET, url, null,
            { response ->
                val responseList = response.getJSONArray("lists")
                val jsonString = responseList.toString()

                val gson = Gson()
                val userLists: List<item> = gson.fromJson(jsonString, object : TypeToken<List<item>>() {}.type)

                val listList = mutableListOf<ListList>()

                userLists.forEachIndexed { _, userLists ->
                    val listItem = ListList(userLists.id.toInt(), userLists.label)
                    listList.add(listItem)
                }

                saveListToPreferences(userLists)

                callback(listList)

                Toast.makeText(context, "Listes récupérées", Toast.LENGTH_LONG).show()

            }, { _ ->
                Toast.makeText(context, "Erreur lors de la récupération des listes", Toast.LENGTH_LONG).show()

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

    fun getItemsListRequest(listId: Int, callback: (MutableList<item>) -> Unit) {
        val url = baseurl + "lists/$listId/items"

        val request = object: JsonObjectRequest(
            Method.GET, url, null, { response ->

                val responseItems = response.getJSONArray("items")
                val jsonString = responseItems.toString()

                val gson = Gson()
                val itemList: MutableList<item> = gson.fromJson(jsonString, object : TypeToken<List<item>>() {}.type)

                saveItemListToPreferences(itemList)

                callback(itemList)

                Toast.makeText(context, "Items récupérés", Toast.LENGTH_LONG).show()

            }, { _ ->
                Toast.makeText(context, "Erreur lors de la récupération des items", Toast.LENGTH_LONG).show()

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

