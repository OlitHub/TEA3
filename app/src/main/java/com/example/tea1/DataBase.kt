import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tea1.ListList
import com.example.tea1.item

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "todo_database.db"
        private const val DATABASE_VERSION = 1

        // Table names
        private const val TABLE_LISTS = "lists"
        private const val TABLE_TODOS = "todos"

        // Common column names
        private const val COLUMN_LIST_ID = "list_id"
        private const val COLUMN_LABEL = "label"

        // List table columns
        private const val USER_NAME = "username"

        // Todos table columns
        private const val COLUMN_TODO_ID = "todo_id"
        private const val COLUMN_URL = "url"
        private const val COLUMN_IS_CHECKED = "is_checked"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the lists table
        val createListsTable = """
            CREATE TABLE $TABLE_LISTS (
                $USER_NAME TEXT NOT NULL,
                $COLUMN_LIST_ID INTEGER PRIMARY KEY NOT NULL,
                $COLUMN_LABEL TEXT NOT NULL
            )
        """
        db.execSQL(createListsTable)

        // Create the todos table
        val createTodosTable = """
            CREATE TABLE $TABLE_TODOS (
                $COLUMN_LIST_ID INTEGER NOT NULL,
                $COLUMN_TODO_ID INTEGER PRIMARY KEY NOT NULL,
                $COLUMN_LABEL TEXT NOT NULL,
                $COLUMN_URL TEXT,
                $COLUMN_IS_CHECKED INTEGER DEFAULT 0,
                FOREIGN KEY ($COLUMN_LIST_ID) REFERENCES $TABLE_LISTS($COLUMN_LIST_ID)
            )
        """
        db.execSQL(createTodosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }

    fun insertList(username: String, listId: Int, label: String?): Long {
        // Check if the list already exists
        if (isListExists(listId)) {
            return -1L // Return -1 to indicate the list already exists
        }

        val values = ContentValues()
        values.put(USER_NAME, username)
        values.put(COLUMN_LIST_ID, listId)
        values.put(COLUMN_LABEL, label ?: "")

        val db = writableDatabase
        val id = db.insert(TABLE_LISTS, null, values)
        db.close()

        return id
    }

    fun insertTodo(
        listId: Int,
        todoId: Int,
        label: String,
        url: String?,
        isChecked: Boolean
    ): Long {
        // Check if the todo already exists
        if (isTodoExists(todoId)) {
            return -1L // Return -1 to indicate the todo already exists
        }

        val values = ContentValues()
        values.put(COLUMN_LIST_ID, listId)
        values.put(COLUMN_TODO_ID, todoId)
        values.put(COLUMN_LABEL, label)
        values.put(COLUMN_URL, url)
        values.put(COLUMN_IS_CHECKED, if (isChecked) 1 else 0)

        val db = writableDatabase
        val id = db.insert(TABLE_TODOS, null, values)
        db.close()

        return id
    }

    private fun isListExists(listId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_LISTS,
            arrayOf(COLUMN_LIST_ID),
            "$COLUMN_LIST_ID = ?",
            arrayOf(listId.toString()),
            null, null, null
        )

        val listExists = cursor.count > 0

        cursor.close()
        db.close()

        return listExists
    }

    private fun isTodoExists(todoId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TODOS,
            arrayOf(COLUMN_TODO_ID),
            "$COLUMN_TODO_ID = ?",
            arrayOf(todoId.toString()),
            null, null, null
        )

        val todoExists = cursor.count > 0

        cursor.close()
        db.close()

        return todoExists
    }

    fun getListsByUsername(username: String): List<ListList> {
        val lists = mutableListOf<ListList>()

        val db = readableDatabase
        val cursor = db.query(
            TABLE_LISTS,
            arrayOf(COLUMN_LIST_ID, COLUMN_LABEL),
            "$USER_NAME = ?",
            arrayOf(username),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val listId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIST_ID))
                val label = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LABEL))
                val list = ListList(listId, label)
                lists.add(list)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return lists
    }

    fun getTodosByListId(listId: Int): MutableList<item> {
        val todos = mutableListOf<item>()
        val db = readableDatabase

        val query = "SELECT $COLUMN_TODO_ID, $COLUMN_LABEL, $COLUMN_URL, $COLUMN_IS_CHECKED FROM $TABLE_TODOS WHERE $COLUMN_LIST_ID = ?"
        val selectionArgs = arrayOf(listId.toString())

        val cursor: Cursor = db.rawQuery(query, selectionArgs)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TODO_ID))
            val label = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LABEL))
            val url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL))
            val isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CHECKED)) == 1

            val todo = item(id, label, url, isChecked)
            todos.add(todo)
        }

        cursor.close()
        db.close()

        return todos
    }

}