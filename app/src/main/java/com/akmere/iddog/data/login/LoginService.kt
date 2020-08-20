package com.akmere.iddog.data.login

import com.akmere.iddog.data.login.model.UserRequest
import com.akmere.iddog.data.login.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("signup")
    suspend fun signUp(@Body userRequest: UserRequest): UserResponse
}