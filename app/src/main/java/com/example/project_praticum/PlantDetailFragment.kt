package com.example.project_praticum

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class PlantDetailFragment : Fragment(R.layout.fragment_plant_detail) {

    private lateinit var sessionManager: SessionManager

    private lateinit var btnClose: ImageView
    private lateinit var btnFavorite: ImageView
    private lateinit var imgProduceDetail: ImageView
    private lateinit var tvPlantName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvPrice: TextView
    private lateinit var btnAddToCart: TextView

    private var tvCareLevel: TextView? = null
    private var tvWatering: TextView? = null

    private var plantId: Int = -1
    private var currentPlant: Plant? = null

    companion object {
        private const val ARG_PLANT_ID = "plant_id"

        fun newInstance(plantId: Int): PlantDetailFragment {
            return PlantDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PLANT_ID, plantId)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        plantId = arguments?.getInt(ARG_PLANT_ID, -1) ?: -1

        bindViews(view)
        setupActions()

        if (plantId != -1) {
            loadPlantDetail()
        } else {
            Toast.makeText(requireContext(), "Plant not found", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun bindViews(view: View) {
        btnClose = view.findViewById(R.id.btnClose)
        btnFavorite = view.findViewById(R.id.btnFavorite)
        imgProduceDetail = view.findViewById(R.id.imgProduceDetail)
        tvPlantName = view.findViewById(R.id.tvPlantName)
        tvCategory = view.findViewById(R.id.tvCategory)
        tvRating = view.findViewById(R.id.tvRating)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvPrice = view.findViewById(R.id.tvPrice)
        btnAddToCart = view.findViewById(R.id.btnAddToCart)

        tvCareLevel = findViewByIdIfExists(view, "tvCareLevel")
        tvWatering = findViewByIdIfExists(view, "tvWatering")
    }

    private fun setupActions() {
        btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnFavorite.setOnClickListener {
            currentPlant?.let { toggleFavorite(it) }
        }

        btnAddToCart.setOnClickListener {
            currentPlant?.let { addToCart(it) }
        }
    }

    private fun loadPlantDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getPlantDetail(
                    token = sessionManager.getBearerToken(),
                    id = plantId
                )

                if (response.isSuccessful && response.body() != null) {
                    currentPlant = response.body()
                    bindPlant(response.body()!!)
                } else {
                    Toast.makeText(requireContext(), "Failed to load plant detail", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Connection error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun bindPlant(plant: Plant) {
        tvPlantName.text = plant.name
        tvCategory.text = plant.category?.name ?: "Plant"
        tvRating.text = "⭐ %.1f (${plant.rating_count ?: 0})".format(plant.rating ?: 0.0)

        tvDescription.text = plant.description
            ?.takeIf { it.isNotBlank() }
            ?: "No description available."

        tvPrice.text = "$ %.2f".format(plant.price)

        tvCareLevel?.text = plant.care_level
            ?.takeIf { it.isNotBlank() }
            ?: "Easy"

        tvWatering?.text = "${plant.watering_frequency ?: 7} days"

        updateFavoriteIcon(plant.is_favorite)
        loadPlantImage(imgProduceDetail, plant.main_image_url)
    }

    private fun toggleFavorite(plant: Plant) {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.toggleWishlist(token, plant.id)

                if (response.isSuccessful && response.body() != null) {
                    val isFavorite = response.body()!!.is_favorite
                    currentPlant = plant.copy(is_favorite = isFavorite)
                    updateFavoriteIcon(isFavorite)

                    Toast.makeText(
                        requireContext(),
                        if (isFavorite) "Added to wishlist" else "Removed from wishlist",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to update favorite", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Connection error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        btnFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_full else R.drawable.ic_favorite
        )
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
                    token = token,
                    request = CartAddRequest(plant.id, 1)
                )

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to add cart", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Connection error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadPlantImage(imageView: ImageView, imageUrl: String?) {
        val finalUrl = imageUrl?.let { convertLocalhostUrl(it) }

        Log.d("PlantDetailImage", "Loading image: $finalUrl")

        Glide.with(imageView.context)
            .load(finalUrl)
            .placeholder(R.drawable.ic_logo)
            .error(R.drawable.ic_logo)
            .into(imageView)
    }

    private fun convertLocalhostUrl(url: String): String {
        return url
            .replace("http://localhost:8000", "http://127.0.0.1:8000")
            .replace("http://10.0.2.2:8000", "http://127.0.0.1:8000")
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : View> findViewByIdIfExists(view: View, idName: String): T? {
        val id = resources.getIdentifier(idName, "id", requireContext().packageName)
        return if (id != 0) view.findViewById(id) as? T else null
    }
}