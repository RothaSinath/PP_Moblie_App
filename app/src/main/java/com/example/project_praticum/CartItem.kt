package com.example.project_praticum

data class CartItem(
    val id: Int,
    val name: String,
    val price: Double,
    var quantity: Int,
    val imageRes: Int
)