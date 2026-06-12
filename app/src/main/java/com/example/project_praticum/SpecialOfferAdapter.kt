package com.example.project_praticum

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SpecialOfferAdapter(
    private val items: MutableList<Plant>,
    private val onPlantClick: (Plant) -> Unit,
    private val onFavoriteClick: (Plant, Int) -> Unit,
    private val onAddCartClick: (Plant) -> Unit
) : RecyclerView.Adapter<SpecialOfferAdapter.SpecialViewHolder>() {

    inner class SpecialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgPlant: ImageView = view.findViewById(R.id.imgPlant)
        private val txtName: TextView = view.findViewById(R.id.txtName)
        private val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        private val txtNewPrice: TextView = view.findViewById(R.id.txtNewPrice)
        private val rating: TextView = view.findViewById(R.id.rating)
        private val ratingCount: TextView = view.findViewById(R.id.number_of_rating)
        private val btnFavorite: ImageView = view.findViewById(R.id.btnFavorite)
        private val btnAdd: ImageButton = view.findViewById(R.id.btnAdd)

        fun bind(item: Plant) {
            txtName.text = item.name
            txtCategory.text = item.category?.name ?: "Plant"
            txtNewPrice.text = "%.2f$".format(item.price)
            rating.text = "%.1f".format(item.rating ?: 0.0)
            ratingCount.text = "(${item.rating_count ?: 0})"

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_plant_card_special_offer, parent, false)

        return SpecialViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpecialViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Plant>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
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