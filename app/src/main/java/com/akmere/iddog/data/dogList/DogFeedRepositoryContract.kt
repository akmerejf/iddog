package com.akmere.iddog.data.dogList

import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.dogList.model.DogFeedResponse

interface DogFeedRepositoryContract {
    suspend fun loadDogFeed(category: String?): Result<DogFeedResponse>
}