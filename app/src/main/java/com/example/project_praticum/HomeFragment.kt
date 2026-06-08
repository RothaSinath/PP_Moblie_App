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
import androidx.viewpager2.widget.MarginPageTransformer

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var sliderPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())

    private val slideRunnable = object : Runnable {
        override fun run() {
            val adapter = sliderPager.adapter ?: return

            if (adapter.itemCount == 0) return

            val nextItem = (sliderPager.currentItem + 1) % adapter.itemCount
            sliderPager.currentItem = nextItem

            handler.postDelayed(this, 3000)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSlider(view)
        setupPlantCards(view)
        setupFilterSelection(view)
    }

    private fun setupSlider(view: View) {
        sliderPager = view.findViewById(R.id.sliderPager)

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

        sliderPager.setPageTransformer(
            MarginPageTransformer(24)
        )
    }

    private fun setupPlantCards(view: View) {
        val card1 = view.findViewById<PlantCardViewSpecialOffer>(R.id.card1)
        val card2 = view.findViewById<PlantCardViewSpecialOffer>(R.id.card2)
        val card3 = view.findViewById<PlantCardViewSpecialOffer>(R.id.card3)

        card1.setPlantName("Cactus")
        card1.setCategoryImage(R.drawable.ic_indoor_plant)
        card1.setCategory("Indoor Plant")
        card1.setRating("5.0")
        card1.setRatingCount("(39)")
        card1.setNewPrice("$5.75")
        card1.setPlantImage(R.drawable.img_cactus)

        card2.setPlantName("Spider plant")
        card2.setCategoryImage(R.drawable.ic_indoor_plant)
        card2.setCategory("Indoor Plant")
        card2.setRating("4.8")
        card2.setRatingCount("(25)")
        card2.setNewPrice("$9.30")
        card2.setPlantImage(R.drawable.img_spider_plant)

        card3.setPlantName("Snake Plant")
        card3.setCategoryImage(R.drawable.ic_outdoor_plant)
        card3.setCategory("Outdoor Plant")
        card3.setRating("4.9")
        card3.setRatingCount("(31)")
        card3.setNewPrice("$9.90")
        card3.setPlantImage(R.drawable.img_snake_plant)
    }

    private fun setupFilterSelection(view: View) {
        val filters = listOf(
            view.findViewById<TextView>(R.id.filter_all),
            view.findViewById<TextView>(R.id.filter_indoor),
            view.findViewById<TextView>(R.id.filter_outdoor),
            view.findViewById<TextView>(R.id.filter_bigtree),
            view.findViewById<TextView>(R.id.filter_aquatic)
        )

        fun selectFilter(selected: TextView) {
            filters.forEach { filter ->
                filter.setBackgroundResource(R.drawable.bg_input)
                filter.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal))
            }

            selected.setBackgroundResource(R.drawable.bg_selected_input)
            selected.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        }

        filters.forEach { filter ->
            filter.setOnClickListener {
                selectFilter(filter)
            }
        }

        selectFilter(filters.first())
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(slideRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(slideRunnable)
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