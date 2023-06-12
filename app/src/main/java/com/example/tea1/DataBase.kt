package com.example.tea1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "database.db"
        private const val DATABASE_VERSION = 1

        // Define your table and column names here
        private const val TABLE_NAME = "mytable"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create your database table(s) here
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_NAME TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle schema updates and data migration here if needed
        // Typically, you would drop and recreate the table(s) or modify the existing schema
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertData(name: String) {
        val db = writableDatabase
        //val values = ContentValues().apply {
        //    put(COLUMN_NAME, name)
        //}
        //db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getData(): List<String> {
        val dataList = mutableListOf<String>()
        val db = readableDatabase
        val query = "SELECT $COLUMN_NAME FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                //val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                //dataList.add(name)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return dataList
    }
}
