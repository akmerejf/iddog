package com.akmere.iddog.di

import android.content.Context
import android.content.SharedPreferences
import com.akmere.iddog.BuildConfig
import com.akmere.iddog.data.common.RemoteErrorHandler
import com.akmere.iddog.data.dogList.*
import com.akmere.iddog.data.login.*
import com.akmere.iddog.domain.Logout
import com.akmere.iddog.domain.LogoutCommand
import com.akmere.iddog.ui.dogfeed.DogFeedViewModel
import com.akmere.iddog.ui.login.LoginViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApplicationModules {

    val loginModule
        get() = module {
            factory { providesLoginRepository(get(), get()) }
            factory { providesLoginDataSourceLocal(get(), get()) }
            factory { providesLoginDataSourceRemote(get(), get()) }
            factory { providesRemoteErrorHandler() }
            factory { providesLoginService(get()) }
            viewModel { LoginViewModel(get()) }
        }

    val dogFeedModule
        get() = module {
            factory { providesDogFeedRepositoryContract(get()) }
            factory { providesDogFeedDataSourceRemote(get(), get()) }
            factory { providesDogFeedService(get()) }
            factory { providesLogoutCommand(get()) }
            viewModel { DogFeedViewModel(get(), get()) }
        }

    private fun providesLogoutCommand(loginRepository: LoginRepositoryContract): LogoutCommand {
        return Logout(loginRepository)
    }

    val networkModule
        get() = module {
            single { providesSharedPreference(androidContext()) }
            single { providesRetrofit(get()) }
            single { providesHttpClient(get()) }
            single { providesGson() }
            single { providesOAuthInterceptor(get()) }
        }

    private fun providesDogFeedService(retrofit: Retrofit): DogFeedService {
        return retrofit.create(DogFeedService::class.java)
    }

    private fun providesOAuthInterceptor(loginDataSourceContract: LoginDataSourceContract.Local): OAuthInterceptor {
        return OAuthInterceptor(loginDataSourceContract)
    }

    private fun providesDogFeedRepositoryContract(remoteDataSource: DogFeedDataSource.Remote): DogFeedRepositoryContract {
        return DogFeedFeedRepository(remoteDataSource)
    }

    private fun providesDogFeedDataSourceRemote(
        dogFeedService: DogFeedService,
        remoteErrorHandler: RemoteErrorHandler
    ): DogFeedDataSource.Remote {
        return DogFeedDataSourceRemote(dogFeedService, remoteErrorHandler)
    }

    private fun providesLoginRepository(
        local: LoginDataSourceContract.Local,
        remote: LoginDataSourceContract.Remote
    ): LoginRepositoryContract =
        LoginRepository(local, remote)

    private fun providesLoginDataSourceRemote(
        loginService: LoginService,
        remoteErrorHandler: RemoteErrorHandler
    ): LoginDataSourceContract.Remote =
        LoginDataSourceRemote(loginService, remoteErrorHandler)

    private fun providesLoginDataSourceLocal(
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): LoginDataSourceContract.Local = LoginDataSourceLocal(sharedPreferences, gson)

    private fun providesSharedPreference(context: Context) =
        context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    private fun providesRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit
            .Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://iddog-nrizncxqba-uc.a.run.app/")
            .build()
    }

    private fun providesRemoteErrorHandler(): RemoteErrorHandler {
        return RemoteErrorHandler()
    }

    private fun providesHttpClient(oAuthInterceptor: OAuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(oAuthInterceptor).build()
    }

    private fun providesGson(): Gson {
        return Gson()
    }

    private fun providesLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }
}


