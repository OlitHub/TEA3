package com.example.tea1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class ApiManager(private val context: Context, private val preferences: SharedPreferences) {

    fun makeLoginRequest(url: String): JsonObjectRequest {

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

                }

            }, { error ->

            })

        return request

    }

    fun addListRequest(list_name: String): JsonObjectRequest {

        val url = "http://tomnab.fr/todo-api/lists?label=$list_name"

        val request = object: JsonObjectRequest(
            Request.Method.POST, url, null, { response ->


            }, { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        return request

    }

    fun userListsRequest(): JsonObjectRequest {
        val url = "http://tomnab.fr/todo-api/lists"

        val request = object: JsonObjectRequest(
            Request.Method.GET, url, null, { response ->

                val responseList = response.getJSONArray("lists")
                val jsonString = responseList.toString()

                val gson = Gson()
                val userLists: List<itemList> = gson.fromJson(jsonString, object : TypeToken<List<itemList>>() {}.type)

                saveListToPreferences(userLists)

            }, { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        return request

    }
    data class itemList(val id: String, val label: String)

    fun saveListToPreferences(list: List<itemList>) {
        val editor: SharedPreferences.Editor = preferences.edit()

        val gson = Gson()
        val json = gson.toJson(list)

        editor.putString("userLists", json)
        editor.apply()
    }

    fun getItemsListRequest(listId: Int): JsonObjectRequest {
        val url = "http://tomnab.fr/todo-api/lists/$listId/items"

        val request = object: JsonObjectRequest(
            Request.Method.GET, url, null, { response ->

                val responseItems = response.getJSONArray("items")
                val jsonString = responseItems.toString()

                val gson = Gson()
                val itemList: List<itemList> = gson.fromJson(jsonString, object : TypeToken<List<itemList>>() {}.type)

                saveItemListToPreferences(itemList)

            }, { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        return request

    }

    fun saveItemListToPreferences(list: List<itemList>) {
        val editor: SharedPreferences.Editor = preferences.edit()

        val gson = Gson()
        val json = gson.toJson(list)

        editor.putString("itemList", json)
        editor.apply()
    }

    fun addListItemRequest(item_name: String, idList: Int?): JsonObjectRequest {

        val url: String = "http://tomnab.fr/todo-api/lists/${idList.toString()}/items?label=$item_name"

        val request = object: JsonObjectRequest(
            Request.Method.POST, url, null, { response ->


            }, { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        return request

    }

    fun checkItemRequest(idItem: String, idList: Int?): JsonObjectRequest {

        val url: String = "http://tomnab.fr/todo-api/lists/$idList/items/$idItem?check=1"

        val request = object: JsonObjectRequest(
            Request.Method.PUT, url, null, { response ->

            }, { error ->
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["hash"] = preferences.getString("token", "")!!
                return headers
            }
        }

        return request

    }

}

