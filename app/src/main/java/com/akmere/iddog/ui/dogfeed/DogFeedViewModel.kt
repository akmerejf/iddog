package com.akmere.iddog.ui.dogfeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.common.IdDogError
import com.akmere.iddog.data.dogList.DogFeedRepositoryContract
import com.akmere.iddog.domain.LogoutCommand

import kotlinx.coroutines.launch
import java.util.Locale

class DogFeedViewModel(
    private val dogFeedFeedRepository: DogFeedRepositoryContract,
    private val logoutInteractor: LogoutCommand
) : ViewModel() {

    private val _dogFeedState = MutableLiveData<DogFeedState>()
    val dogFeedState: LiveData<DogFeedState> = _dogFeedState

    fun logout(){
        logoutInteractor.execute()
        _dogFeedState.value = DogFeedState.LoginRequired
    }
    fun loadDogFeed(category: String? = null) {
        _dogFeedState.value = DogFeedState.Loading

        viewModelScope.launch {
            val result = dogFeedFeedRepository
                .loadDogFeed(
                    category
                        ?.toLowerCase(Locale.ROOT)
                )

            if (result is Result.Success) {
                _dogFeedState.value =
                    DogFeedState.Success(result.data.list)
            } else if (result is Result.Error) {
                if (result.exception is IdDogError.UnAuthorizedIdDogError) {
                   logout()
                } else {
                    _dogFeedState.value =
                        DogFeedState.Failure(result.exception.errorMessage)
                }
            }
        }
    }

}