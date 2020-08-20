package com.akmere.iddog.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.akmere.iddog.IdDogApplication
import com.akmere.iddog.di.ApplicationModules
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

@ExperimentalCoroutinesApi
abstract class BaseTest : KoinTest {
    private val applicationContext = mockk<IdDogApplication>(relaxed = true)

    @get:Rule
    val instantExecutorTask = InstantTaskExecutorRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(applicationContext)
        modules(
            ApplicationModules.networkModule,
            ApplicationModules.dogFeedModule,
            ApplicationModules.loginModule
        )
    }
    val dispatcher = TestCoroutineDispatcher()

    @Before
    open fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)
    }

    @After
    open fun teardown() {
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
        stopKoin()
    }
}
