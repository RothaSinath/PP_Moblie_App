package com.example.yourapp

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.example.project_praticum.R
import com.google.android.material.card.MaterialCardView

class PlantCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val imgPlant: ImageView

    private val imgCategory: ImageView
    private val txtCategory: TextView
    private val txtName: TextView

    private val rating: TextView

    private val numberOfRating: TextView
    private val txtNewPrice: TextView
    private val btnAdd: ImageButton

    init {
        radius = dpToPx(28f)
        cardElevation = dpToPx(6f)
        setCardBackgroundColor(resources.getColor(R.color.dark_teal, null))
        useCompatPadding = true

        val root = RelativeLayout(context).apply {
            layoutParams = RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }

        LayoutInflater.from(context).inflate(R.layout.card_plant_view, root, true)
        addView(root)

        imgPlant = findViewById(R.id.imgPlant)
        imgCategory = findViewById(R.id.imgCategory)
        txtCategory = findViewById(R.id.txtCategory)
        txtName = findViewById(R.id.txtName)
        rating = findViewById(R.id.rating)
        numberOfRating = findViewById(R.id.number_of_rating)
        txtNewPrice = findViewById(R.id.txtNewPrice)
        btnAdd = findViewById(R.id.btnAdd)


    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    fun setPlantName(name: String) {
        txtName.text = name
    }

    fun setCategory(category: String) {
        txtCategory.text = category
    }

    fun setRating(rating: String) {
        this.rating.text = rating
    }

    fun setNumberOfRating(numberOfRating: String) {
        this.numberOfRating.text = numberOfRating
    }


    fun setNewPrice(newPrice: String) {
        txtNewPrice.text = newPrice
    }

    fun setPlantImage(@DrawableRes imageRes: Int) {
        imgPlant.setImageResource(imageRes)
    }

    fun setCategoryImage(@DrawableRes imageRes: Int) {
        imgCategory.setImageResource(imageRes)
    }


    fun setOnAddClickListener(listener: OnClickListener) {
        btnAdd.setOnClickListener(listener)
    }
}