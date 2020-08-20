package com.akmere.iddog.data.dogList

import com.akmere.iddog.data.dogList.model.DogFeedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DogFeedService {
    @GET("feed")
    suspend fun loadFeed(@Query("category") category: String?): DogFeedResponse
}