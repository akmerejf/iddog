package com.akmere.iddog.viewmodel

import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.common.IdDogError
import com.akmere.iddog.data.dogList.DogFeedDataSource
import com.akmere.iddog.data.dogList.DogFeedRepositoryContract
import com.akmere.iddog.data.dogList.model.DogFeedResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.inject

@ExperimentalCoroutinesApi
class DogFeedRepositoryTest : BaseTest() {
    @MockK(relaxed = true)
    private lateinit var dogFeedDataSourceRemote: DogFeedDataSource.Remote

    private var mockCategory = "husky"

    private val subject: DogFeedRepositoryContract by inject()


    override fun setup() {
        super.setup()
        loadKoinModules(
            module {
                factory(override = true) {
                    dogFeedDataSourceRemote
                }
            }
        )
    }

    @Test
    fun `When successful loadDogFeed then Result should return DogFeedResponse`() =
        runBlockingTest {
            val mockDogFeedResponse: DogFeedResponse = mockk(relaxed = true)
            val expectedResult = Result.Success(mockDogFeedResponse)
            coEvery { dogFeedDataSourceRemote.fetchDogFeed(mockCategory) } returns expectedResult

            val result = subject.loadDogFeed(mockCategory)

            assertEquals(expectedResult, result)

            coVerify(exactly = 1) {
                dogFeedDataSourceRemote.fetchDogFeed(mockCategory)
            }

        }

    @Test
    fun `When failed loadDogFeed then Result should return Error`() =
        runBlockingTest {
            val mockError: IdDogError = mockk(relaxed = true)
            val expectedResult = Result.Error(mockError)
            coEvery { dogFeedDataSourceRemote.fetchDogFeed(mockCategory) } returns expectedResult

            val result = subject.loadDogFeed(mockCategory)

            assertEquals(expectedResult, result)

            coVerify(exactly = 1) {
                dogFeedDataSourceRemote.fetchDogFeed(mockCategory)
            }

        }
}