package com.akmere.iddog.data.dogList

import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.common.RemoteErrorHandler
import com.akmere.iddog.data.dogList.model.DogFeedResponse

class DogFeedDataSourceRemote(
    private val dogFeedService: DogFeedService,
    private val remoteErrorHandler: RemoteErrorHandler
) : DogFeedDataSource.Remote {
    override suspend fun fetchDogFeed(category: String?): Result<DogFeedResponse> {
        return try {
            val dogFeedRequest = dogFeedService.loadFeed(category)
            Result.Success(dogFeedRequest)
        } catch (error: Exception) {
            Result.Error(remoteErrorHandler.handleRequestErrors(error))
        }
    }

}