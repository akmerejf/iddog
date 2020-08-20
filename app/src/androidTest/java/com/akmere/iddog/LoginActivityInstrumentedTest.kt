package com.akmere.iddog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.akmere.iddog.ui.dogfeed.DogFeedViewModel
import com.akmere.iddog.ui.login.*
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LoginActivityInstrumentedTest : KoinTest {

    @get:Rule
    val instantExecutorTask = InstantTaskExecutorRule()

    @get:Rule
    var activityRule: ActivityTestRule<LoginActivity> =
        ActivityTestRule(LoginActivity::class.java, true, false)

    @MockK(relaxed = true)
    private lateinit var loginViewModel: LoginViewModel

    @MockK(relaxed = true)
    private lateinit var dogFeedViewModel: DogFeedViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        loadKoinModules(module {
            viewModel { loginViewModel }
            viewModel { dogFeedViewModel }
        })
    }

    @After
    fun tearDown() {
        activityRule.finishActivity()
        stopKoin()
    }

    private val loginResult = MutableLiveData<LoginResult>()
    private val loginFormState = MutableLiveData<LoginFormState>()

    @Test
    fun loginSuccessful() {
        val mockEmail = "test@test.com"

        every { loginViewModel.loginResult } returns loginResult
        every { loginViewModel.loginFormState } returns loginFormState
        every { loginViewModel.loginDataChanged(any()) } returns Unit

        activityRule.launchActivity(null)

        onView(withId(R.id.email)).perform(
            typeText(mockEmail),
            closeSoftKeyboard()
        )
        activityRule.runOnUiThread {
            loginFormState.postValue(LoginFormState(emailError = null, isDataValid = true))

        }
        onView(withId(R.id.login)).check(matches(isEnabled()))

        onView(withId(R.id.login)).perform(click())


        onView(withId(R.id.loading))
            .check(matches(isDisplayed()))

        activityRule.runOnUiThread {
            loginResult.postValue(LoginResult(LoggedInUserView(mockEmail)))
        }
        onView(withId(R.id.loading))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(
            withText(
                containsString(
                    activityRule.activity.getString(
                        R.string.welcome,
                        mockEmail
                    )
                )
            )
        ).inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }
}



