package com.example.project_praticum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(
    private val items: MutableList<CartResponse>,
    private val onPlusClick: (CartResponse) -> Unit,
    private val onMinusClick: (CartResponse) -> Unit,
    private val onDeleteClick: (CartResponse) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        private val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = view.findViewById(R.id.tvProductPrice)
        private val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        private val btnPlus: TextView = view.findViewById(R.id.btnPlus)
        private val btnMinus: TextView = view.findViewById(R.id.btnMinus)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

        fun bind(item: CartResponse) {
            val plant = item.plant

            tvProductName.text = plant.name
            tvProductPrice.text = "$ %.2f".format(plant.price)
            tvQuantity.text = item.quantity.toString()

            val imageUrl = plant.main_image_url?.let { convertLocalhostUrl(it) }

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .fitCenter()
                .into(imgProduct)

            btnPlus.setOnClickListener {
                onPlusClick(item)
            }

            btnMinus.setOnClickListener {
                onMinusClick(item)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)

        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<CartResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun updateItem(updatedItem: CartResponse) {
        val index = items.indexOfFirst { it.id == updatedItem.id }

        if (index != -1) {
            items[index] = updatedItem
            notifyItemChanged(index)
        }
    }

    fun removeItem(item: CartResponse) {
        val index = items.indexOfFirst { it.id == item.id }

        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    private fun convertLocalhostUrl(url: String): String {
        return url
            .replace("http://localhost:8000", "http://127.0.0.1:8000")
            .replace("http://10.0.2.2:8000", "http://127.0.0.1:8000")
    }
}