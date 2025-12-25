package com.oyn.rencangku.data

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("authToken")
    val authToken: String
)
