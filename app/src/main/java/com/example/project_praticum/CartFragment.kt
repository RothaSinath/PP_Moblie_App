package com.example.project_praticum

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var recyclerCart: RecyclerView
    private lateinit var tvSubtotalValue: TextView
    private lateinit var tvDeliveryValue: TextView
    private lateinit var tvTotalValue: TextView
    private lateinit var btnCheckout: MaterialButton

    private lateinit var cartAdapter: CartAdapter

    private val cartItems = mutableListOf(
        CartItem(1, "Cactus", 5.75, 1, R.drawable.img_cactus),
        CartItem(2, "Spider plant", 9.30, 1, R.drawable.img_spider_plant),
        CartItem(3, "Snake Plant", 9.90, 1, R.drawable.img_snake_plant)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerCart = view.findViewById(R.id.recyclerCart)
        tvSubtotalValue = view.findViewById(R.id.tvSubtotalValue)
        tvDeliveryValue = view.findViewById(R.id.tvDeliveryValue)
        tvTotalValue = view.findViewById(R.id.tvTotalValue)
        btnCheckout = view.findViewById(R.id.btnCheckout)

        setupRecyclerView()
        setupActions()
        updateSummary()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            items = cartItems,
            onPlusClick = { item ->
                item.quantity++
                cartAdapter.notifyDataSetChanged()
                updateSummary()
            },
            onMinusClick = { item ->
                if (item.quantity > 1) {
                    item.quantity--
                    cartAdapter.notifyDataSetChanged()
                    updateSummary()
                }
            },
            onDeleteClick = { item ->
                val position = cartItems.indexOf(item)
                if (position != -1) {
                    cartItems.removeAt(position)
                    cartAdapter.notifyItemRemoved(position)
                    updateSummary()
                }
            }
        )

        recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        recyclerCart.adapter = cartAdapter
        recyclerCart.isNestedScrollingEnabled = false
    }

    private fun setupActions() {
        btnCheckout.setOnClickListener {
            // Later: call checkout API here
        }
    }

    private fun updateSummary() {
        val subtotal = cartItems.sumOf { it.price * it.quantity }
        val delivery = if (cartItems.isEmpty()) 0.0 else 0.65
        val total = subtotal + delivery

        tvSubtotalValue.text = "$ %.2f".format(subtotal)
        tvDeliveryValue.text = "$ %.2f".format(delivery)
        tvTotalValue.text = "$ %.2f".format(total)
    }
}