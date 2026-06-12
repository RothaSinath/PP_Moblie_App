package com.example.project_praticum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlantBigCardAdapter(
    private val items: MutableList<Plant>,
    private val showCloseButton: Boolean,
    private val onPlantClick: (Plant) -> Unit,
    private val onCloseClick: (Plant, Int) -> Unit,
    private val onAddCartClick: (Plant) -> Unit
) : RecyclerView.Adapter<PlantBigCardAdapter.PlantBigViewHolder>() {

    inner class PlantBigViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgPlant: ImageView = view.findViewById(R.id.imgPlant)
        private val btnClose: ImageView = view.findViewById(R.id.btnClose)
        private val tvRating: TextView = view.findViewById(R.id.tvRating)
        private val tvPlantName: TextView = view.findViewById(R.id.tvPlantName)
        private val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        private val btnAddToCart: TextView = view.findViewById(R.id.btnAddToCart)

        fun bind(item: Plant) {
            tvRating.text = "%.1f".format(item.rating ?: 0.0)
            tvPlantName.text = item.name
            tvPrice.text = "$%.2f".format(item.price)

            btnClose.visibility = if (showCloseButton) View.VISIBLE else View.GONE

            val imageUrl = item.main_image_url?.let { convertLocalhostUrl(it) }

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .centerCrop()
                .into(imgPlant)

            itemView.setOnClickListener {
                onPlantClick(item)
            }

            btnClose.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCloseClick(item, position)
                }
            }

            btnAddToCart.setOnClickListener {
                onAddCartClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantBigViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant_big_card, parent, false)

        return PlantBigViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantBigViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Plant>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position in items.indices) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun convertLocalhostUrl(url: String): String {
        return url
            .replace("http://localhost:8000", "http://127.0.0.1:8000")
            .replace("http://10.0.2.2:8000", "http://127.0.0.1:8000")
    }
}