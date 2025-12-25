package com.oyn.rencangku.data

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val authToken: String,
    val user: User
)