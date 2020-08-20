package com.akmere.iddog.data.dogList

import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.dogList.model.DogFeedResponse

interface DogFeedDataSource {
    interface Local

    interface Remote{
        suspend fun fetchDogFeed(category: String?): Result<DogFeedResponse>
    }
}