package com.akmere.iddog.ui.dogfeed

sealed class DogFeedState {
    data class Success(val dogImages: List<String>) : DogFeedState()
    data class Failure(val errorMessage: String) : DogFeedState()
    object Loading : DogFeedState()
    object LoginRequired : DogFeedState()
}
