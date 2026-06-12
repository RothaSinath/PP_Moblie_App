package com.example.project_praticum

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlantHorizontalAdapter(
    private val items: MutableList<Plant>,
    private val onPlantClick: (Plant) -> Unit,
    private val onFavoriteClick: (Plant, Int) -> Unit,
    private val onAddCartClick: (Plant) -> Unit
) : RecyclerView.Adapter<PlantHorizontalAdapter.PlantViewHolder>() {

    inner class PlantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgPlant: ImageView = view.findViewById(R.id.imgPlant)
        private val tvPlantName: TextView = view.findViewById(R.id.tvPlantName)
        private val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        private val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        private val btnFavorite: ImageView = view.findViewById(R.id.btnFavorite)
        private val btnAdd: TextView = view.findViewById(R.id.btnAdd)

        fun bind(item: Plant) {
            tvPlantName.text = item.name
            tvCategory.text = "🪴 ${item.category?.name ?: "Plant"}"
            tvPrice.text = "%.2f$".format(item.price)

            loadPlantImage(imgPlant, item.main_image_url)

            btnFavorite.setImageResource(
                if (item.is_favorite) R.drawable.ic_favorite_full else R.drawable.ic_favorite
            )

            itemView.setOnClickListener {
                onPlantClick(item)
            }

            btnFavorite.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavoriteClick(item, position)
                }
            }

            btnAdd.setOnClickListener {
                onAddCartClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_plant, parent, false)

        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Plant>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun updateFavorite(position: Int, isFavorite: Boolean) {
        if (position in items.indices) {
            items[position].is_favorite = isFavorite
            notifyItemChanged(position)
        }
    }

    private fun loadPlantImage(imageView: ImageView, imageUrl: String?) {
        val finalUrl = imageUrl?.let { convertLocalhostUrl(it) }

        Log.d("PlantImage", "Loading image: $finalUrl")

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
}