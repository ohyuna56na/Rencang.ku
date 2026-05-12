package com.oyn.rencangku.ml

data class RecommendationResponse(
    val user_id: Int,
    val recommendations: List<RecommendationItem>
)