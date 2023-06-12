package com.example.tea1

import com.google.gson.annotations.SerializedName
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley

data class ListList(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
)

data class item(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("isChecked") val checked: Boolean
)


class ListListAdapter(private val itemList: MutableList<ListList>) : RecyclerView.Adapter<ListListAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    // Définit une interface pour gérer les événements de clic
    interface OnItemClickListener {
        fun onItemClick(listItem: ListList?)
    }

    // Méthode pour définir le listener de clic
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val listItem = itemList[position]
                    onItemClickListener?.onItemClick(listItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_listes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.nameTextView.text = item?.name
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }
}

class ItemAdapter(private val items: List<item>, private val listId: Int?) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private lateinit var apiManager: ApiManager

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemCheckBox: CheckBox = view.findViewById(R.id.itemCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemCheckBox.text = item.name
    //    val context = holder.itemView.context
    //
    //    holder.itemCheckBox.setOnCheckedChangeListener {_, isChecked ->
    //        if(holder.itemCheckBox.isChecked) {
    //            val requestQueue = Volley.newRequestQueue(context)
    //            val request = apiManager.checkItemRequest(item.id.toString(), this.listId)
    //            requestQueue.add(request)
    //        }
    //
    //    }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
