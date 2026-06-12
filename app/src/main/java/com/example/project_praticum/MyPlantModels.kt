package com.example.project_praticum

data class MyPlantResponse(
    val id: Int,
    val user_id: Int,
    val plant_id: Int?,
    val custom_name: String?,
    val custom_image: String?,
    val expert_tip: String?,
    val source: String,
    val plant: Plant?
)

data class MyPlantAddResponse(
    val message: String?,
    val data: MyPlantResponse
)

data class ExpertTipRequest(
    val expert_tip: String
)

data class ExpertTipResponse(
    val message: String?,
    val data: MyPlantResponse
)