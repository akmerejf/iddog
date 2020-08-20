package com.akmere.iddog.data.dogList.model


import com.google.gson.annotations.SerializedName

data class DogFeedResponse(
    @SerializedName("category")
    val category: String,
    @SerializedName("list")
    val list: List<String>
)