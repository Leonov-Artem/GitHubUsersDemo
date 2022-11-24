package com.training.android.githubusersdemo.model.data.source.remote.providers

import com.training.android.githubusersdemo.model.data.source.remote.Accept
import com.training.android.githubusersdemo.model.data.source.remote.ClientId
import com.training.android.githubusersdemo.model.data.source.remote.ClientSecret
import com.training.android.githubusersdemo.model.data.source.remote.GitHubApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class GitHubApiProvider {

    abstract val api: GitHubApi

    protected abstract val acceptHeaderValue: String

    protected abstract val baseUrl: String

    protected abstract val clientBuilder: OkHttpClient.Builder

    protected val clientId = ClientId("641c3206e799fb6bbf7b")

    protected val clientSecret = ClientSecret("098688270e7049c04210dbdd719aedca353a54fd")

    protected val retrofit: Retrofit
        get() {
            val client = clientBuilder
                .addInterceptor(headerInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

    private val headerInterceptor: Interceptor
        get() = Interceptor { chain ->
            val acceptHeader = Accept(acceptHeaderValue)

            val newRequest = chain.request().newBuilder()
                .addHeader(acceptHeader.name, acceptHeader.value)
                .build()
            return@Interceptor chain.proceed(newRequest)
        }

    private val loggingInterceptor: Interceptor
        get() = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
}