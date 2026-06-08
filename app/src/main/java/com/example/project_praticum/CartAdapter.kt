package com.example.project_praticum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onPlusClick: (CartItem) -> Unit,
    private val onMinusClick: (CartItem) -> Unit,
    private val onDeleteClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        private val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = view.findViewById(R.id.tvProductPrice)
        private val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        private val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        private val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

        fun bind(item: CartItem) {
            imgProduct.setImageResource(item.imageRes)
            tvProductName.text = item.name
            tvProductPrice.text = "$ %.2f".format(item.price)
            tvQuantity.text = item.quantity.toString()

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
}