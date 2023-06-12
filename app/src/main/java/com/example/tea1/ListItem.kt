package com.example.tea1

import android.content.Context
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
    @SerializedName("label") val label: String?,
)

data class item(
    @SerializedName("id") val id: Int,
    @SerializedName("label") val label: String?,
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

    fun updateList(newList: List<ListList>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
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
        holder.nameTextView.text = item?.label
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }
}

class ItemAdapter(private val items: MutableList<item>, val listId: Int = 0) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private lateinit var apiManager: ApiManager

    fun updateList(itemList: MutableList<item>) {
        items.clear()
        items.addAll(itemList)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemCheckBox: CheckBox = view.findViewById(R.id.itemCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item: item = items[position]
        holder.itemCheckBox.text = item.label
        holder.itemCheckBox.isChecked = item.checked

    //    holder.itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
    //        if (isChecked) {
    //            apiManager.checkItemRequest(item.id.toString(), this.listId)
    //        } else {
    //            apiManager.unCheckItemRequest(item.id.toString(), this.listId)
    //        }
    //    }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
