package com.example.project_praticum

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MyPlantFragment : Fragment(R.layout.fragment_my_plant) {

    private lateinit var sessionManager: SessionManager

    private lateinit var edtSearchPlant: EditText
    private lateinit var btnWishlistPlant: ImageView
    private lateinit var btnMore: ImageView
    private lateinit var recyclerMyPlants: RecyclerView
    private lateinit var tvEmptyPlant: TextView
    private lateinit var btnAddPlant: TextView

    private lateinit var myPlantAdapter: MyPlantAdapter

    private val myPlants = mutableListOf<MyPlantResponse>()
    private val wishlistPlants = mutableListOf<Plant>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        bindViews(view)
        setupRecyclerView()
        setupActions()

        if (myPlants.isNotEmpty()) {
            renderMyPlants()
        } else {
            loadMyPlants()
        }
    }

    private fun bindViews(view: View) {
        edtSearchPlant = view.findViewById(R.id.edtSearchPlant)
        btnWishlistPlant = view.findViewById(R.id.btnWishlistPlant)
        btnMore = view.findViewById(R.id.btnMore)
        recyclerMyPlants = view.findViewById(R.id.recyclerMyPlants)
        tvEmptyPlant = view.findViewById(R.id.tvEmptyPlant)
        btnAddPlant = view.findViewById(R.id.btnAddPlant)
    }

    private fun setupRecyclerView() {
        myPlantAdapter = MyPlantAdapter(
            items = mutableListOf(),
            onPlantClick = { item ->
                openMyPlantDetail(item.id)
            }
        )

        recyclerMyPlants.layoutManager = LinearLayoutManager(requireContext())
        recyclerMyPlants.adapter = myPlantAdapter
    }

    private fun setupActions() {
        btnAddPlant.setOnClickListener {
            showAddPlantImageDialog()
        }

        btnWishlistPlant.setOnClickListener {
            loadWishlistForPicker()
        }

        btnMore.setOnClickListener {
            Toast.makeText(requireContext(), "More options will be added later", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMyPlants() {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getMyPlants(token)

                if (response.isSuccessful && response.body() != null) {
                    myPlants.clear()
                    myPlants.addAll(response.body()!!)
                    renderMyPlants()
                } else {
                    val error = response.errorBody()?.string()
                    Toast.makeText(
                        requireContext(),
                        "Failed to load My Plant: ${response.code()} $error",
                        Toast.LENGTH_LONG
                    ).show()
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

    private fun renderMyPlants() {
        myPlantAdapter.updateData(myPlants)
        tvEmptyPlant.visibility = if (myPlants.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showAddPlantImageDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")

        AlertDialog.Builder(requireContext())
            .setTitle("Add Plant")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openScanPage()
                    1 -> openScanPage()
                }
            }
            .show()
    }

    private fun openScanPage() {
        startActivity(Intent(requireContext(), ScanActivity::class.java))
    }

    private fun loadWishlistForPicker() {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getWishlist(token)

                if (response.isSuccessful && response.body() != null) {
                    wishlistPlants.clear()
                    wishlistPlants.addAll(response.body()!!)

                    if (wishlistPlants.isEmpty()) {
                        Toast.makeText(requireContext(), "Your wishlist is empty", Toast.LENGTH_SHORT).show()
                    } else {
                        showWishlistPicker()
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Toast.makeText(
                        requireContext(),
                        "Failed to load wishlist: ${response.code()} $error",
                        Toast.LENGTH_LONG
                    ).show()
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

    private fun showWishlistPicker() {
        val plantNames = wishlistPlants.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Add from Wishlist")
            .setItems(plantNames) { _, which ->
                addWishlistPlantToMyPlant(wishlistPlants[which])
            }
            .show()
    }

    private fun addWishlistPlantToMyPlant(plant: Plant) {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.addMyPlantFromWishlist(token, plant.id)

                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "${plant.name} added to My Plant",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadMyPlants()
                } else {
                    val error = response.errorBody()?.string()
                    Toast.makeText(
                        requireContext(),
                        "Failed to add plant: ${response.code()} $error",
                        Toast.LENGTH_LONG
                    ).show()
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

    private fun openMyPlantDetail(id: Int) {
        val containerId = (requireView().parent as? ViewGroup)?.id

        if (containerId == null || containerId == View.NO_ID) {
            Toast.makeText(requireContext(), "Fragment container not found", Toast.LENGTH_SHORT).show()
            return
        }

        parentFragmentManager.beginTransaction()
            .replace(containerId, MyPlantDetailFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }
}