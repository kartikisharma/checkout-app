package kartiki.checkoutapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kartiki.checkoutapp.network.Item
import kotlinx.android.synthetic.main.viewholder_item.view.*

class ItemsAdapter : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {
    private lateinit var mItems : List<Item>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Item) {
            itemView.apply {
                itemName.text = item.name
                when (item.available) {
                    true -> {
                        updateImageViewColor(android.R.color.holo_green_dark)
                        availabilityStatus.setText(R.string.item_available)
                    }
                    else -> {
                        updateImageViewColor(android.R.color.holo_red_dark)
                        availabilityStatus.setText(R.string.item_unavailable)
                    }
                }
            }
        }

        private fun updateImageViewColor(@ColorRes id: Int) {
            itemView.imageView.setColorFilter(
                ContextCompat.getColor(itemView.context, id),
                android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    fun setItems(items: List<Item>) {
        mItems = items
        notifyDataSetChanged()
    }
}