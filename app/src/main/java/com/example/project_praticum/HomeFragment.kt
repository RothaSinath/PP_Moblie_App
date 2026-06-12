package com.example.project_praticum

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var sessionManager: SessionManager

    private lateinit var sliderPager: ViewPager2
    private lateinit var imgProfile: ImageView
    private lateinit var tvGreeting: TextView
    private lateinit var edtSearch: EditText
    private lateinit var btnFavoritePage: ImageView

    private lateinit var sectionSpecial: View
    private lateinit var sectionPopular: View
    private lateinit var sectionIndoor: View
    private lateinit var sectionOutdoor: View
    private lateinit var sectionAquatic: View
    private lateinit var sectionBigTree: View

    private lateinit var specialAdapter: SpecialOfferAdapter
    private lateinit var popularAdapter: PlantHorizontalAdapter
    private lateinit var indoorAdapter: CategoryPlantAdapter
    private lateinit var outdoorAdapter: CategoryPlantAdapter
    private lateinit var aquaticAdapter: CategoryPlantAdapter
    private lateinit var bigTreeAdapter: CategoryPlantAdapter

    private val allPlants = mutableListOf<Plant>()
    private val handler = Handler(Looper.getMainLooper())

    private var selectedFilter = "All"
    private var searchJob: Job? = null

    private val slideRunnable = object : Runnable {
        override fun run() {
            val adapter = sliderPager.adapter ?: return

            if (adapter.itemCount > 0) {
                val nextItem = (sliderPager.currentItem + 1) % adapter.itemCount
                sliderPager.currentItem = nextItem
            }

            handler.postDelayed(this, 3000)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        bindViews(view)
        setupSlider()
        setupSections()
        setupFilters(view)
        setupSearch()
        setupActions()

        parentFragmentManager.setFragmentResultListener("wishlist_changed", viewLifecycleOwner) { _, _ ->
            loadPlants()
        }

        loadProfile()
        loadPlants()
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
        loadPlants()
        handler.postDelayed(slideRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(slideRunnable)
    }

    private fun bindViews(view: View) {
        sliderPager = view.findViewById(R.id.sliderPager)
        imgProfile = view.findViewById(R.id.imgProfile)
        tvGreeting = view.findViewById(R.id.tvGreeting)
        edtSearch = view.findViewById(R.id.edtSearch)
        btnFavoritePage = view.findViewById(R.id.btnFavoritePage)

        sectionSpecial = view.findViewById(R.id.sectionSpecial)
        sectionPopular = view.findViewById(R.id.sectionPopular)
        sectionIndoor = view.findViewById(R.id.sectionIndoor)
        sectionOutdoor = view.findViewById(R.id.sectionOutdoor)
        sectionAquatic = view.findViewById(R.id.sectionAquatic)
        sectionBigTree = view.findViewById(R.id.sectionBigTree)
    }

    private fun setupSlider() {
        val images = listOf(
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3,
            R.drawable.banner_4
        )

        sliderPager.adapter = BannerAdapter(images)
        sliderPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        sliderPager.offscreenPageLimit = 1
        sliderPager.clipToPadding = false
        sliderPager.clipChildren = false
        sliderPager.setPadding(20, 0, 20, 0)
        sliderPager.setPageTransformer(MarginPageTransformer(24))
    }

    private fun setupSections() {
        specialAdapter = createSpecialAdapter()
        popularAdapter = createPopularAdapter()
        indoorAdapter = createCategoryAdapter()
        outdoorAdapter = createCategoryAdapter()
        aquaticAdapter = createCategoryAdapter()
        bigTreeAdapter = createCategoryAdapter()

        setupSection(sectionSpecial, "Special Offer", specialAdapter, "Special")
        setupSection(sectionPopular, "Popular Plants", popularAdapter, "Popular")
        setupSection(sectionIndoor, "Indoor", indoorAdapter, "Indoor")
        setupSection(sectionOutdoor, "Outdoor", outdoorAdapter, "Outdoor")
        setupSection(sectionAquatic, "Aquatic", aquaticAdapter, "Aquatic")
        setupSection(sectionBigTree, "Big Tree", bigTreeAdapter, "Big Tree")
    }

    private fun setupSection(
        section: View,
        title: String,
        adapter: RecyclerView.Adapter<*>,
        seeMoreCategory: String
    ) {
        val tvTitle = section.findViewById<TextView>(R.id.tvSectionTitle)
        val btnSeeMore = section.findViewById<TextView>(R.id.btnSeeMore)
        val recycler = section.findViewById<RecyclerView>(R.id.recyclerPlants)

        tvTitle.text = title

        recycler.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recycler.adapter = adapter
        recycler.clipToPadding = false
        recycler.clipChildren = false

        btnSeeMore.setOnClickListener {
            openPlantList(seeMoreCategory)
        }
    }

    private fun createSpecialAdapter(): SpecialOfferAdapter {
        return SpecialOfferAdapter(
            items = mutableListOf(),
            onPlantClick = { plant ->
                openPlantDetail(plant.id)
            },
            onFavoriteClick = { plant, _ ->
                toggleFavorite(plant)
            },
            onAddCartClick = { plant ->
                addToCart(plant)
            }
        )
    }

    private fun createPopularAdapter(): PlantHorizontalAdapter {
        return PlantHorizontalAdapter(
            items = mutableListOf(),
            onPlantClick = { plant ->
                openPlantDetail(plant.id)
            },
            onFavoriteClick = { plant, _ ->
                toggleFavorite(plant)
            },
            onAddCartClick = { plant ->
                addToCart(plant)
            }
        )
    }

    private fun createCategoryAdapter(): CategoryPlantAdapter {
        return CategoryPlantAdapter(
            items = mutableListOf(),
            onPlantClick = { plant ->
                openPlantDetail(plant.id)
            },
            onFavoriteClick = { plant, _ ->
                toggleFavorite(plant)
            },
            onAddCartClick = { plant ->
                addToCart(plant)
            }
        )
    }

    private fun setupFilters(view: View) {
        val filters = mapOf(
            "All" to view.findViewById<TextView>(R.id.filter_all),
            "Indoor" to view.findViewById<TextView>(R.id.filter_indoor),
            "Outdoor" to view.findViewById<TextView>(R.id.filter_outdoor),
            "Aquatic" to view.findViewById<TextView>(R.id.filter_aquatic),
            "Big Tree" to view.findViewById<TextView>(R.id.filter_bigtree)
        )

        filters.forEach { (name, textView) ->
            textView.setOnClickListener {
                selectedFilter = name
                updateFilterUi(filters, textView)
                renderPlants()
            }
        }

        updateFilterUi(filters, filters["All"]!!)
    }

    private fun updateFilterUi(filters: Map<String, TextView>, selected: TextView) {
        filters.values.forEach { filter ->
            filter.setBackgroundResource(R.drawable.bg_input)
            filter.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal))
        }

        selected.setBackgroundResource(R.drawable.bg_selected_input)
        selected.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
    }

    private fun setupSearch() {
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    loadPlants()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupActions() {
        btnFavoritePage.setOnClickListener {
            openWishlist()
        }
    }

    private fun loadProfile() {
        val name = sessionManager.getUserName()
        val avatarUrl = sessionManager.getUserAvatarUrl()

        tvGreeting.text = "Hi! $name"

        if (!avatarUrl.isNullOrBlank()) {
            val finalUrl = convertLocalhostUrl(avatarUrl) + "?t=${System.currentTimeMillis()}"

            Log.d("ProfileImage", "Loading profile: $finalUrl")

            Glide.with(imgProfile.context)
                .load(finalUrl)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .circleCrop()
                .into(imgProfile)
        } else {
            imgProfile.setImageResource(R.drawable.profile)
        }

        loadProfileFromApi()
    }

    private fun loadProfileFromApi() {
        val token = sessionManager.getBearerToken() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getUser(token)

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    sessionManager.saveUser(user)

                    val finalUrl = user.avatar_url
                        ?.let { convertLocalhostUrl(it) }
                        ?.plus("?t=${System.currentTimeMillis()}")

                    Log.d("ProfileImage", "API profile: $finalUrl")

                    Glide.with(imgProfile.context)
                        .load(finalUrl)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .circleCrop()
                        .into(imgProfile)
                }
            } catch (e: Exception) {
                Log.e("ProfileImage", "Failed to load profile", e)
            }
        }
    }

    private fun loadPlants() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getPlants(
                    token = sessionManager.getBearerToken(),
                    search = edtSearch.text.toString().trim().ifBlank { null },
                    category = null
                )

                if (response.isSuccessful && response.body() != null) {
                    allPlants.clear()
                    allPlants.addAll(response.body()!!)
                    renderPlants()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load plants",
                        Toast.LENGTH_SHORT
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

    private fun renderPlants() {
        val specialPlants = allPlants
            .filter { it.is_special_offer }
            .take(4)

        val popularPlants = allPlants
            .sortedByDescending { it.rating ?: 0.0 }
            .take(4)

        val indoorPlants = allPlants
            .filter { isIndoor(it.category?.name) }
            .take(4)

        val outdoorPlants = allPlants
            .filter { isOutdoor(it.category?.name) }
            .take(4)

        val aquaticPlants = allPlants
            .filter { isAquatic(it.category?.name) }
            .take(4)

        val bigTreePlants = allPlants
            .filter { isBigTree(it.category?.name) }
            .take(4)

        sectionSpecial.visibility = if (selectedFilter == "All") View.VISIBLE else View.GONE
        sectionPopular.visibility = if (selectedFilter == "All") View.VISIBLE else View.GONE
        sectionIndoor.visibility = if (selectedFilter == "All" || selectedFilter == "Indoor") View.VISIBLE else View.GONE
        sectionOutdoor.visibility = if (selectedFilter == "All" || selectedFilter == "Outdoor") View.VISIBLE else View.GONE
        sectionAquatic.visibility = if (selectedFilter == "All" || selectedFilter == "Aquatic") View.VISIBLE else View.GONE
        sectionBigTree.visibility = if (selectedFilter == "All" || selectedFilter == "Big Tree") View.VISIBLE else View.GONE

        specialAdapter.updateData(specialPlants)
        popularAdapter.updateData(popularPlants)
        indoorAdapter.updateData(indoorPlants)
        outdoorAdapter.updateData(outdoorPlants)
        aquaticAdapter.updateData(aquaticPlants)
        bigTreeAdapter.updateData(bigTreePlants)
    }

    private fun isIndoor(categoryName: String?): Boolean {
        val value = categoryName?.trim()?.lowercase() ?: return false
        return value == "indoor" || value == "indoor plant"
    }

    private fun isOutdoor(categoryName: String?): Boolean {
        val value = categoryName?.trim()?.lowercase() ?: return false
        return value == "outdoor" || value == "outdoor plant"
    }

    private fun isAquatic(categoryName: String?): Boolean {
        val value = categoryName?.trim()?.lowercase() ?: return false
        return value == "aquatic" || value == "aquatic plant"
    }

    private fun isBigTree(categoryName: String?): Boolean {
        val value = categoryName?.trim()?.lowercase() ?: return false
        return value == "big tree" || value == "bigtree" || value == "big tree plant"
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

                    val index = allPlants.indexOfFirst { it.id == plant.id }
                    if (index != -1) {
                        allPlants[index].is_favorite = isFavorite
                    }

                    renderPlants()

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

    private fun openPlantDetail(plantId: Int) {
        val containerId = getContainerId() ?: return

        parentFragmentManager.beginTransaction()
            .replace(containerId, PlantDetailFragment.newInstance(plantId))
            .addToBackStack(null)
            .commit()
    }

    private fun openPlantList(category: String) {
        val containerId = getContainerId() ?: return

        parentFragmentManager.beginTransaction()
            .replace(containerId, PlantListFragment.newInstance(category))
            .addToBackStack(null)
            .commit()
    }

    private fun openWishlist() {
        val containerId = getContainerId() ?: return

        parentFragmentManager.beginTransaction()
            .replace(containerId, WishlistFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun getContainerId(): Int? {
        val containerId = (requireView().parent as? ViewGroup)?.id

        if (containerId == null || containerId == View.NO_ID) {
            Toast.makeText(
                requireContext(),
                "Fragment container not found",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }

        return containerId
    }

    private fun convertLocalhostUrl(url: String): String {
        return url
            .replace("http://localhost:8000", "http://127.0.0.1:8000")
            .replace("http://10.0.2.2:8000", "http://127.0.0.1:8000")
    }

    private class BannerAdapter(
        private val imageList: List<Int>
    ) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

        class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.image_banner_item)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_banner_slider, parent, false)

            return BannerViewHolder(view)
        }

        override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
            holder.imageView.setImageResource(imageList[position])
        }

        override fun getItemCount(): Int = imageList.size
    }
}