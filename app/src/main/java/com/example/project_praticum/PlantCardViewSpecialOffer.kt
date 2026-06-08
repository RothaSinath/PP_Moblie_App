package com.example.project_praticum

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

class PlantCardViewSpecialOffer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val imgPlant: ImageView
    private val imgCategory: ImageView
    private val txtName: TextView
    private val txtCategory: TextView
    private val txtNewPrice: TextView
    private val txtRating: TextView
    private val txtRatingCount: TextView
    private val btnAdd: ImageButton
    private val btnFavorite: ImageView

    init {
        LayoutInflater.from(context).inflate(
            R.layout.view_plant_card_special_offer,
            this,
            true
        )

        imgPlant = findViewById(R.id.imgPlant)
        imgCategory = findViewById(R.id.imgCategory)
        txtName = findViewById(R.id.txtName)
        txtCategory = findViewById(R.id.txtCategory)
        txtNewPrice = findViewById(R.id.txtNewPrice)
        txtRating = findViewById(R.id.rating)
        txtRatingCount = findViewById(R.id.number_of_rating)
        btnAdd = findViewById(R.id.btnAdd)
        btnFavorite = findViewById(R.id.btnFavorite)
    }

    fun setPlantName(name: String) {
        txtName.text = name
    }

    fun setCategory(category: String) {
        txtCategory.text = category
    }

    fun setCategoryImage(imageRes: Int) {
        imgCategory.setImageResource(imageRes)
    }

    fun setPlantImage(imageRes: Int) {
        imgPlant.setImageResource(imageRes)
    }

    fun setNewPrice(price: String) {
        txtNewPrice.text = price
    }

    fun setRating(rating: String) {
        txtRating.text = rating
    }

    fun setRatingCount(count: String) {
        txtRatingCount.text = count
    }

    fun setOnAddClickListener(listener: OnClickListener) {
        btnAdd.setOnClickListener(listener)
    }

    fun setOnFavoriteClickListener(listener: OnClickListener) {
        btnFavorite.setOnClickListener(listener)
    }
}