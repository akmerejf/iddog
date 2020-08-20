package com.akmere.iddog.data.dogList

import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.dogList.model.DogFeedResponse

class DogFeedFeedRepository(private val remote: DogFeedDataSource.Remote): DogFeedRepositoryContract {

   override suspend fun loadDogFeed(category: String?): Result<DogFeedResponse> {
        return remote.fetchDogFeed(category)
    }

}