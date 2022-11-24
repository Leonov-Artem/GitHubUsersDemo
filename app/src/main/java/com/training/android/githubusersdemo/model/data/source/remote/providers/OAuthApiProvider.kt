package com.training.android.githubusersdemo.model.data.source.remote.providers

import com.training.android.githubusersdemo.model.data.source.remote.OAuthApi
import com.training.android.githubusersdemo.model.data.source.remote.Query
import okhttp3.Interceptor
import okhttp3.OkHttpClient


object OAuthApiProvider : GitHubApiProvider() {

    override val acceptHeaderValue = "application/json"

    override val baseUrl = "https://github.com/"

    override val clientBuilder: OkHttpClient.Builder
        get() = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor(bodyInterceptor)

    override val api: OAuthApi
        get() = retrofit.create(OAuthApi::class.java)

    private val bodyInterceptor: Interceptor
        get() = Interceptor { chain ->
            val originalRequest = chain.request()

            val newUrl = originalRequest.url.newBuilder()
                .addQueryParameter(clientId.name, clientId.value)
                .addQueryParameter(clientSecret.name, clientSecret.value)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            return@Interceptor chain.proceed(newRequest)
        }

    object SignInUrl {

        private val scope = Query(name = "scope", value = "read:user")

        private var baseAuthorizationUrl = "${baseUrl}login/oauth/authorize?$clientId&$scope"

        fun create(login: String): String {
            createLoginQuery(login)?.let { loginQuery ->
                baseAuthorizationUrl += "&$loginQuery"
            }
            return baseAuthorizationUrl
        }

        private fun createLoginQuery(login: String): Query? {
            return if (login.isNotBlank()) Query(name = "login", value = login) else null
        }
    }
}