package com.training.android.githubusersdemo.model.data.source.remote.providers

import com.training.android.githubusersdemo.model.data.source.remote.TokenValidityApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient

private val TAG = TokenValidityApiProvider::class.java.simpleName

object TokenValidityApiProvider : GitHubApiProvider() {

    override val acceptHeaderValue = "application/vnd.github+json"

    override val baseUrl = "https://api.github.com/"

    override val clientBuilder: OkHttpClient.Builder
        get() = OkHttpClient.Builder()
//            .addInterceptor(UrlInterceptor)

    override val api: TokenValidityApi
        get() = retrofit.create(TokenValidityApi::class.java)

    private val UrlInterceptor: Interceptor
        get() = Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url

            val newUrl = originalUrl.newBuilder()
                .setPathSegment(
                    index = originalUrl.pathSegments.indexOf(TokenValidityApi.CLIENT_ID_PATH_SEGMENT),
                    pathSegment = clientId.value,
                )
                .username(clientId.value)
                .password(clientSecret.value)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            return@Interceptor chain.proceed(newRequest)
        }
}