package com.example.project_praticum

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class PlantListFragment : Fragment(R.layout.fragment_plant_list) {

    private lateinit var sessionManager: SessionManager
    private lateinit var tvTitle: TextView
    private lateinit var recyclerPlants: RecyclerView
    private lateinit var adapter: PlantBigCardAdapter


    private val category: String by lazy {
        arguments?.getString(ARG_CATEGORY) ?: "All"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        tvTitle = view.findViewById(R.id.tvTitle)
        recyclerPlants = view.findViewById(R.id.recyclerPlants)

        tvTitle.text = category

        adapter = PlantBigCardAdapter(
            items = mutableListOf(),
            showCloseButton = false,
            onPlantClick = { plant ->
                openPlantDetail(plant.id)
            },
            onCloseClick = { _, _ -> },
            onAddCartClick = { plant ->
                addToCart(plant)
            }
        )

        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        recyclerPlants.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerPlants.adapter = adapter

        loadPlants()
    }

    private fun loadPlants() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getPlants(
                    token = sessionManager.getBearerToken(),
                    category = if (category == "Popular" || category == "Special") null else category
                )

                if (response.isSuccessful && response.body() != null) {
                    val plants = when (category) {
                        "Popular" -> response.body()!!.sortedByDescending { it.rating ?: 0.0 }
                        "Special" -> response.body()!!.filter { it.is_special_offer }
                        else -> response.body()!!
                    }

                    adapter.updateData(plants)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toggleFavorite(plant: Plant) {
        val token = sessionManager.getBearerToken() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            ApiClient.apiService.toggleWishlist(token, plant.id)
            loadPlants()
        }
    }

    private fun addToCart(plant: Plant) {
        val token = sessionManager.getBearerToken() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            ApiClient.apiService.addToCart(token, CartAddRequest(plant.id, 1))
            Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPlantDetail(id: Int) {
        val containerId = (requireView().parent as? ViewGroup)?.id ?: return

        parentFragmentManager.beginTransaction()
            .replace(containerId, PlantDetailFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: String): PlantListFragment {
            val fragment = PlantListFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_CATEGORY, category)
            }
            return fragment
        }
    }
}