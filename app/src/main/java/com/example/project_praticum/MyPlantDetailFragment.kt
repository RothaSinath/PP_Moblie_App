package com.example.project_praticum

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class MyPlantDetailFragment : Fragment(R.layout.fragment_my_plant_detail) {

    private lateinit var sessionManager: SessionManager

    private lateinit var btnBack: ImageView
    private lateinit var imgPlant: ImageView
    private lateinit var tvPlantName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvCareInfo: TextView
    private lateinit var edtExpertTip: EditText
    private lateinit var btnSaveTip: TextView

    private var myPlantId: Int = -1
    private var currentItem: MyPlantResponse? = null

    companion object {
        private const val ARG_MY_PLANT_ID = "my_plant_id"

        fun newInstance(id: Int): MyPlantDetailFragment {
            return MyPlantDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MY_PLANT_ID, id)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        myPlantId = arguments?.getInt(ARG_MY_PLANT_ID, -1) ?: -1

        bindViews(view)
        setupActions()
        loadDetail()
    }

    private fun bindViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        imgPlant = view.findViewById(R.id.imgPlant)
        tvPlantName = view.findViewById(R.id.tvPlantName)
        tvCategory = view.findViewById(R.id.tvCategory)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvCareInfo = view.findViewById(R.id.tvCareInfo)
        edtExpertTip = view.findViewById(R.id.edtExpertTip)
        btnSaveTip = view.findViewById(R.id.btnSaveTip)
    }

    private fun setupActions() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSaveTip.setOnClickListener {
            saveExpertTip()
        }
    }

    private fun loadDetail() {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getMyPlants(token)

                if (response.isSuccessful && response.body() != null) {
                    currentItem = response.body()!!.firstOrNull { it.id == myPlantId }

                    if (currentItem == null) {
                        Toast.makeText(requireContext(), "Plant not found", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        bindData(currentItem!!)
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load plant", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Connection error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun bindData(item: MyPlantResponse) {
        val plant = item.plant

        tvPlantName.text = plant?.name ?: item.custom_name ?: "My Plant"
        tvCategory.text = plant?.category?.name ?: item.source

        tvDescription.text = plant?.description
            ?.takeIf { it.isNotBlank() }
            ?: "No description available."

        tvCareInfo.text = buildString {
            append("Care level: ${plant?.care_level ?: "Easy"}")
            append("\nWatering: every ${plant?.watering_frequency ?: 7} days")
            append("\nSunlight: ${plant?.sunlight_requirement ?: "Indirect Light"}")
        }

        edtExpertTip.setText(item.expert_tip ?: "")

        val imageUrl = plant?.main_image_url?.let { convertLocalhostUrl(it) }

        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.ic_logo)
            .error(R.drawable.ic_logo)
            .fitCenter()
            .into(imgPlant)
    }

    private fun saveExpertTip() {
        val token = sessionManager.getBearerToken()

        if (token == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        val tip = edtExpertTip.text.toString().trim()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.updateMyPlantExpertTip(
                    token = token,
                    id = myPlantId,
                    request = ExpertTipRequest(tip)
                )

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Expert tip saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to save tip", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Connection error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun convertLocalhostUrl(url: String): String {
        return url
            .replace("http://localhost:8000", "http://127.0.0.1:8000")
            .replace("http://10.0.2.2:8000", "http://127.0.0.1:8000")
    }
}