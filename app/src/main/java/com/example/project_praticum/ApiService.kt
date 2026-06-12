package com.example.project_praticum

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("register")
    suspend fun register(
        @Body request: SignupRequest
    ): Response<AuthResponse>

    @GET("user")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): Response<User>

    @Multipart
    @POST("profile/update")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody?,
        @Part("password_confirmation") passwordConfirmation: RequestBody?,
        @Part avatar: MultipartBody.Part?
    ): Response<ProfileUpdateResponse>

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("plants")
    suspend fun getPlants(
        @Header("Authorization") token: String?,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null
    ): Response<List<Plant>>

    @GET("plants/{id}")
    suspend fun getPlantDetail(
        @Header("Authorization") token: String?,
        @Path("id") id: Int
    ): Response<Plant>

    @GET("wishlist")
    suspend fun getWishlist(
        @Header("Authorization") token: String
    ): Response<List<Plant>>

    @POST("wishlist/toggle/{id}")
    suspend fun toggleWishlist(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<WishlistToggleResponse>

    @POST("cart/add")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: CartAddRequest
    ): Response<Unit>

    @GET("cart")
    suspend fun getCart(
        @Header("Authorization") token: String
    ): Response<List<CartResponse>>

    @PUT("cart/{id}")
    suspend fun updateCartItem(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: CartUpdateRequest
    ): Response<CartResponse>

    @DELETE("cart/{id}")
    suspend fun deleteCartItem(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @GET("my-plants")
    suspend fun getMyPlants(
        @Header("Authorization") token: String
    ): Response<List<MyPlantResponse>>

    @POST("my-plants/from-wishlist/{plant}")
    suspend fun addMyPlantFromWishlist(
        @Header("Authorization") token: String,
        @Path("plant") plantId: Int
    ): Response<MyPlantAddResponse>

    @POST("my-plants/{id}/expert-tip")
    suspend fun updateMyPlantExpertTip(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: ExpertTipRequest
    ): Response<ExpertTipResponse>

    @DELETE("my-plants/{id}")
    suspend fun deleteMyPlant(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}