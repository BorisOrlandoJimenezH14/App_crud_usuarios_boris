package com.borisjh.appcruduser.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("tokenType") val tokenType: String,
    @SerializedName("expiresIn") val expiresIn: Long
)

data class UserRequest(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

data class UpdateUserRequest(
    val name: String,
    val email: String,
    val password: String?,
    val role: String,
    val status: String
)

data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val status: String
)

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}

interface UserApiService {
    @GET("api/users")
    suspend fun getAllUsers(): List<UserResponse>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserResponse

    @POST("api/users")
    suspend fun createUser(@Body request: UserRequest): UserResponse

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body request: UpdateUserRequest): UserResponse

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Void>
}
