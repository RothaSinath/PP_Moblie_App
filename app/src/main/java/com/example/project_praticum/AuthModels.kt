package com.example.project_praticum

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class AuthResponse(
    val message: String?,
    val user: User,
    val token: String
)

data class ProfileUpdateResponse(
    val message: String?,
    val user: User
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val avatar: String?,
    val avatar_url: String?,
    val role: String
)

data class Category(
    val id: Int,
    val name: String,
    val icon: String?
)

data class Plant(
    val id: Int,
    val category_id: Int?,
    val name: String,
    val scientific_name: String?,
    val description: String?,
    val expert_tip: String?,
    val price: Double,
    val old_price: Double?,
    val stock: Int?,
    val main_image: String?,
    val main_image_url: String?,
    val rating: Double?,
    val rating_count: Int?,
    val care_level: String?,
    val watering_frequency: Int?,
    val sunlight_requirement: String?,
    val is_best_seller: Boolean,
    val is_special_offer: Boolean,
    var is_favorite: Boolean,
    val category: Category?
)

data class WishlistToggleResponse(
    val message: String?,
    val is_favorite: Boolean
)

data class CartAddRequest(
    val plant_id: Int,
    val quantity: Int
)

data class CartUpdateRequest(
    val quantity: Int
)

data class CartResponse(
    val id: Int,
    val user_id: Int,
    val plant_id: Int,
    var quantity: Int,
    val plant: Plant
)