package com.akmere.iddog.data.login

import android.util.Log
import com.akmere.iddog.data.common.Result
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class OAuthInterceptor(private val loginDataSourceLocal: LoginDataSourceContract.Local) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val userResult = loginDataSourceLocal.loadUser()
        var requestCopy: Request = chain.request()

        if (userResult is Result.Success) {
            userResult.data.token.let { accessToken ->
                requestCopy = requestCopy.newBuilder()
                    .addHeader(
                        "Authorization",
                        accessToken
                    )
                    .build()
            }
        }

        Log.d(
            "OkHttp", String.format(
                "--> Sending request %s on %s%n%s",
                requestCopy.url(),
                chain.connection(),
                requestCopy.headers()
            )
        )

        return chain.proceed(requestCopy).apply {
            if (code() == 401)
                loginDataSourceLocal.deleteUser()
        }

    }


}