package com.example.project_praticum

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var sessionManager: SessionManager

    private lateinit var btnBack: ImageButton
    private lateinit var recyclerCart: RecyclerView
    private lateinit var tvSubtotalValue: TextView
    private lateinit var tvDeliveryValue: TextView
    private lateinit var tvTotalValue: TextView
    private lateinit var btnCheckout: MaterialButton

    private lateinit var cartAdapter: CartAdapter

    private val cartItems = mutableListOf<CartResponse>()
    private val deliveryFee = 0.65

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        btnBack = view.findViewById(R.id.btnBack)
        recyclerCart = view.findViewById(R.id.recyclerCart)
        tvSubtotalValue = view.findViewById(R.id.tvSubtotalValue)
        tvDeliveryValue = view.findViewById(R.id.tvDeliveryValue)
        tvTotalValue = view.findViewById(R.id.tvTotalValue)
        btnCheckout = view.findViewById(R.id.btnCheckout)

        setupRecyclerView()
        setupActions()
        updateSummary()
        loadCart()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            items = cartItems,
            onPlusClick = { item ->
                updateCartQuantity(item, item.quantity + 1)
            },
            onMinusClick = { item ->
                if (item.quantity > 1) {
                    updateCartQuantity(item, item.quantity - 1)
                } else {
                    deleteCartItem(item)
                }
            },
            onDeleteClick = { item ->
                deleteCartItem(item)
            }
        )

        recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        recyclerCart.adapter = cartAdapter
        recyclerCart.isNestedScrollingEnabled = false
    }

    private fun setupActions() {
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnCheckout.setOnClickListener {
            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Checkout will be added next", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCart() {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getCart(token)

                if (response.isSuccessful && response.body() != null) {
                    val items = response.body()!!

                    cartItems.clear()
                    cartItems.addAll(items)
                    cartAdapter.updateData(items)

                    updateSummary()
                } else {
                    Toast.makeText(requireContext(), "Failed to load cart", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateCartQuantity(item: CartResponse, quantity: Int) {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        val oldQuantity = item.quantity

        item.quantity = quantity
        cartAdapter.notifyDataSetChanged()
        updateSummary()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.updateCartItem(
                    token = token,
                    id = item.id,
                    request = CartUpdateRequest(quantity)
                )

                if (response.isSuccessful && response.body() != null) {
                    val updatedItem = response.body()!!

                    val index = cartItems.indexOfFirst { it.id == updatedItem.id }
                    if (index != -1) {
                        cartItems[index] = updatedItem
                    }

                    cartAdapter.updateItem(updatedItem)
                    updateSummary()
                } else {
                    item.quantity = oldQuantity
                    cartAdapter.notifyDataSetChanged()
                    updateSummary()

                    Toast.makeText(requireContext(), "Failed to update cart", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                item.quantity = oldQuantity
                cartAdapter.notifyDataSetChanged()
                updateSummary()

                Toast.makeText(
                    requireContext(),
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun deleteCartItem(item: CartResponse) {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        cartItems.removeAll { it.id == item.id }
        cartAdapter.removeItem(item)
        updateSummary()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.deleteCartItem(
                    token = token,
                    id = item.id
                )

                if (!response.isSuccessful) {
                    Toast.makeText(requireContext(), "Failed to delete item", Toast.LENGTH_SHORT).show()
                    loadCart()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                loadCart()
            }
        }
    }

    private fun updateSummary() {
        val subtotal = cartItems.sumOf { it.plant.price * it.quantity }
        val delivery = if (cartItems.isEmpty()) 0.0 else deliveryFee
        val total = subtotal + delivery

        tvSubtotalValue.text = "$ %.2f".format(subtotal)
        tvDeliveryValue.text = "$ %.2f".format(delivery)
        tvTotalValue.text = "$ %.2f".format(total)
    }
}