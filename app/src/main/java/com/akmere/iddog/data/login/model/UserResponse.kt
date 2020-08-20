package com.akmere.iddog.data.login.model


import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("user")
    val user: User
)