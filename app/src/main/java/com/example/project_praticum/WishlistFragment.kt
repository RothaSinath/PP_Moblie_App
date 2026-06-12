package com.example.project_praticum

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.GridLayoutManager

class WishlistFragment : Fragment(R.layout.fragment_wishlist) {

    private lateinit var sessionManager: SessionManager
    private lateinit var recyclerWishlist: RecyclerView
    private lateinit var adapter: PlantBigCardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        recyclerWishlist = view.findViewById(R.id.recyclerWishlist)

        view.findViewById<ImageView>(R.id.btnCloseWishlist).setOnClickListener {
            parentFragmentManager.setFragmentResult(
                "wishlist_changed",
                bundleOf("changed" to true)
            )
            parentFragmentManager.popBackStack()
        }

        adapter = PlantBigCardAdapter(
            items = mutableListOf(),
            showCloseButton = true,
            onPlantClick = { plant ->
                openPlantDetail(plant.id)
            },
            onCloseClick = { plant, position ->
                removeFromWishlist(plant, position)
            },
            onAddCartClick = { plant ->
                addToCart(plant)
            }
        )

        recyclerWishlist.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerWishlist.adapter = adapter

        loadWishlist()
    }

    private fun loadWishlist() {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getWishlist(token)

                if (response.isSuccessful && response.body() != null) {
                    val plants = response.body()!!.map {
                        it.copy(is_favorite = true)
                    }

                    adapter.updateData(plants)
                } else {
                    Toast.makeText(requireContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show()
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

    private fun removeFromWishlist(plant: Plant, position: Int) {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        adapter.removeItem(position)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.toggleWishlist(token, plant.id)

                if (response.isSuccessful && response.body() != null) {
                    val isFavorite = response.body()!!.is_favorite

                    parentFragmentManager.setFragmentResult(
                        "wishlist_changed",
                        bundleOf("changed" to true)
                    )

                    if (!isFavorite) {
                        Toast.makeText(requireContext(), "Removed from wishlist", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Still in wishlist", Toast.LENGTH_SHORT).show()
                        loadWishlist()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to update favorite", Toast.LENGTH_SHORT).show()
                    loadWishlist()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                loadWishlist()
            }
        }
    }

    private fun addToCart(plant: Plant) {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.addToCart(
                    token,
                    CartAddRequest(plant.id, 1)
                )

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to add cart", Toast.LENGTH_SHORT).show()
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

    private fun openPlantDetail(id: Int) {
        val containerId = (requireView().parent as? ViewGroup)?.id

        if (containerId == null || containerId == View.NO_ID) {
            Toast.makeText(requireContext(), "Fragment container not found", Toast.LENGTH_SHORT).show()
            return
        }

        parentFragmentManager.beginTransaction()
            .replace(containerId, PlantDetailFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }
}