package com.example.project_praticum

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.yourapp.PlantCardView
import com.example.yourapp.PlantCardViewSpecialOffer

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var sliderPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())

    private val slideRunnable = object : Runnable {
        override fun run() {
            val adapter = sliderPager.adapter ?: return
            if (adapter.itemCount == 0) return // Safety check
            val nextItem = (sliderPager.currentItem + 1) % adapter.itemCount
            sliderPager.currentItem = nextItem
            handler.postDelayed(this, 3000)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. ViewPager initialization
        sliderPager = view.findViewById(R.id.sliderPager)

        val images = listOf(
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3,
            R.drawable.banner_4
        )

        // 2. FIXED: Attach a custom Adapter so the images can render
        sliderPager.adapter = BannerAdapter(images)
        sliderPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        sliderPager.offscreenPageLimit = 1

        // Plant Cards setup
        val card1 = view.findViewById<PlantCardViewSpecialOffer>(R.id.card1)
        val card2 = view.findViewById<PlantCardViewSpecialOffer>(R.id.card2)
        val card3 = view.findViewById<PlantCardViewSpecialOffer>(R.id.card3)

        card1.setPlantName("Cactus")
        card1.setCategoryImage(R.drawable.ic_indoor_plant)
        card1.setCategory("Indoor Plant")
        card1.setOldPrice("$7.75")
        card1.setNewPrice("$5.75")
        card1.setPlantImage(R.drawable.ic_logo_1)

        card2.setPlantName("Mango")
        card2.setCategoryImage(R.drawable.ic_indoor_plant)
        card2.setCategory("Indoor Plant")
        card2.setOldPrice("$9.50")
        card2.setNewPrice("$6.90")
        card2.setPlantImage(R.drawable.ic_logo_2)

        card3.setPlantName("Snowy Spider Plant")
        card3.setCategoryImage(R.drawable.ic_outdoor_plant)
        card3.setCategory("Outdoor Plant")
        card3.setOldPrice("$12.00")
        card3.setNewPrice("$8.40")
        card3.setPlantImage(R.drawable.ic_logo_2)

        setupFilterSelection(view)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(slideRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(slideRunnable)
    }

    // ================= 3. FIXED: ADD THE BANNER ADAPTER LAYER =================
    private class BannerAdapter(private val imageList: List<Int>) :
        RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

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

    // ================= FILTER LOGIC =================
    private fun setupFilterSelection(view: View) {
        val filters = listOf(
            view.findViewById<TextView>(R.id.filter_all),
            view.findViewById<TextView>(R.id.filter_indoor),
            view.findViewById<TextView>(R.id.filter_outdoor),
            view.findViewById<TextView>(R.id.filter_bigtree),
            view.findViewById<TextView>(R.id.filter_aquatic)
        )

        fun selectFilter(selected: TextView) {
            filters.forEach {
                it.setBackgroundResource(R.drawable.bg_input)
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal))
            }
            selected.setBackgroundResource(R.drawable.bg_selected_input)
            selected.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        }

        filters.forEach { filter ->
            filter.setOnClickListener { selectFilter(filter) }
        }
        selectFilter(filters.first())
    }
}