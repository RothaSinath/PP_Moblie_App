package com.example.project_praticum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyPlantAdapter(
    private val items: MutableList<MyPlantResponse>,
    private val onPlantClick: (MyPlantResponse) -> Unit
) : RecyclerView.Adapter<MyPlantAdapter.MyPlantViewHolder>() {

    inner class MyPlantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgPlant: ImageView = view.findViewById(R.id.imgPlant)
        private val tvPlantName: TextView = view.findViewById(R.id.tvPlantName)
        private val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        private val tvExpertTipStatus: TextView = view.findViewById(R.id.tvExpertTipStatus)

        fun bind(item: MyPlantResponse) {
            val plant = item.plant

            tvPlantName.text = plant?.name ?: item.custom_name ?: "My Plant"
            tvCategory.text = plant?.category?.name ?: item.source

            tvExpertTipStatus.text = if (item.expert_tip.isNullOrBlank()) {
                "No expert tip yet"
            } else {
                "Expert tip added"
            }

            val imageUrl = plant?.main_image_url?.let { convertLocalhostUrl(it) }

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .fitCenter()
                .into(imgPlant)

            itemView.setOnClickListener {
                onPlantClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_plant, parent, false)

        return MyPlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPlantViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MyPlantResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    private fun convertLocalhostUrl(url: String): String {
        return url
            .replace("http://localhost:8000", "http://127.0.0.1:8000")
            .replace("http://10.0.2.2:8000", "http://127.0.0.1:8000")
    }
}