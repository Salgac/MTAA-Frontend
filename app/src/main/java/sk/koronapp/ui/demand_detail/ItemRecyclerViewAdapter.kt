package sk.koronapp.ui.demand_detail

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.koronapp.R
import sk.koronapp.models.Item

class ItemRecyclerViewAdapter(
    private val context: Context,
    private val items: MutableList<Item>
) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    private val itemsMap: HashMap<String, Item> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.demand_detail_list_item, parent, false)
        for (item in items) {
            itemsMap.put(item.name, item)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.itemQuantity.text = item.quantity.toString()
        holder.itemUnit.text = item.unit
        holder.itemPrice.text = item.price.toString() + "€"
    }

    fun addItem(item: Item): Boolean {
        if (itemsMap.containsKey(item.name))
            return false
        items.add(item)
        itemsMap[item.name] = item
        this.notifyItemChanged(items.size - 1)
        return true
    }

    fun getItems(): List<Item> {
        return items
    }

    private fun onItemLongClick(position: Int): Boolean {
        val dialog = AlertDialog.Builder(context)
        dialog.setMessage("Do you want to delete item " + items[position].name + "?")
        dialog.setPositiveButton("Yes") { dialog, which ->
            items.removeAt(position)
            this.notifyItemRemoved(position)
            dialog.dismiss()
        }
        dialog.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }
        dialog.show()
        return true
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnLongClickListener {
                onItemLongClick(adapterPosition)
            }
        }

        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemQuantity: TextView = view.findViewById(R.id.item_quantity)
        val itemUnit: TextView = view.findViewById(R.id.item_unit)
        val itemPrice: TextView = view.findViewById(R.id.item_price)

        override fun toString(): String {
            return itemName.text.toString()
        }
    }

}