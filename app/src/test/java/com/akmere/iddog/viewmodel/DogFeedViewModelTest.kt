package com.akmere.iddog.viewmodel

import androidx.lifecycle.Observer
import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.common.IdDogError
import com.akmere.iddog.data.dogList.DogFeedRepositoryContract
import com.akmere.iddog.data.dogList.model.DogFeedResponse
import com.akmere.iddog.domain.LogoutCommand
import com.akmere.iddog.ui.dogfeed.DogFeedState
import com.akmere.iddog.ui.dogfeed.DogFeedViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.inject

@ExperimentalCoroutinesApi
class DogFeedViewModelTest : BaseTest() {

    @MockK
    private lateinit var dogFeedFeedRepository: DogFeedRepositoryContract
    @MockK
    private lateinit var logoutInteractor: LogoutCommand

    private var mockCategory: String? = null

    private var mockDogFeedResponse = DogFeedResponse("", listOf())

    private val subject: DogFeedViewModel by inject()

    override fun setup() {
        super.setup()
        loadKoinModules(
            module {
                factory(override = true) { dogFeedFeedRepository }
                factory(override = true) { logoutInteractor }
            }
        )
    }

    @Test
    fun `When load dog feed then DogFeedState should return DogFeedState Success`() =
        dispatcher.runBlockingTest {
            val observer = spyk<Observer<DogFeedState>>()
            subject.dogFeedState.observeForever(observer)

            coEvery { dogFeedFeedRepository.loadDogFeed(mockCategory) } returns Result.Success(
                mockDogFeedResponse
            )

            subject.loadDogFeed(mockCategory)

            assert(subject.dogFeedState.value is DogFeedState.Success)

            verifyOrder {
                observer.onChanged(DogFeedState.Loading)
                observer.onChanged(DogFeedState.Success(mockDogFeedResponse.list))
            }
        }

    @Test
    fun `When load dog feed then DogFeedState should return DogFeedState Failure`() =
        dispatcher.runBlockingTest {
            val observer = spyk<Observer<DogFeedState>>()
            subject.dogFeedState.observeForever(observer)
            val mockError = IdDogError.IdDogNotFoundError("")
            coEvery { dogFeedFeedRepository.loadDogFeed(mockCategory) } returns Result.Error(
                mockError
            )

            subject.loadDogFeed(mockCategory)

            assert(subject.dogFeedState.value is DogFeedState.Failure)

            coVerifyOrder {
                observer.onChanged(DogFeedState.Loading)
                dogFeedFeedRepository.loadDogFeed(mockCategory)
                observer.onChanged(DogFeedState.Failure(mockError.errorMessage))
            }
        }

    @Test
    fun `When load dog feed then DogFeedState should return DogFeedState LodingRequired`() =
        dispatcher.runBlockingTest {
            val observer = spyk<Observer<DogFeedState>>()
            subject.dogFeedState.observeForever(observer)

            val mockError = IdDogError.UnAuthorizedIdDogError("")

            coEvery { dogFeedFeedRepository.loadDogFeed(mockCategory) } returns Result.Error(
                mockError
            )
            every { logoutInteractor.execute() } returns Unit

            subject.loadDogFeed(mockCategory)

            assert(subject.dogFeedState.value is DogFeedState.LoginRequired)

            coVerifyOrder {
                observer.onChanged(DogFeedState.Loading)
                dogFeedFeedRepository.loadDogFeed(mockCategory)
                observer.onChanged(DogFeedState.LoginRequired)
            }
        }
}