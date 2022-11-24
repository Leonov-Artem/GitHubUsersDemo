package com.training.android.githubusersdemo.model.data.source.remote.providers

import android.content.Context
import android.util.Log
import com.training.android.githubusersdemo.helper.AccessTokenDataStore
import com.training.android.githubusersdemo.model.data.source.remote.Authorization
import com.training.android.githubusersdemo.model.data.source.remote.UsersApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient

private val TAG = UsersApiProvider::class.java.simpleName

class UsersApiProvider(
    private val dataStore: AccessTokenDataStore,
) : GitHubApiProvider() {

    override val acceptHeaderValue = "application/vnd.github+json"

    override val baseUrl = "https://api.github.com/"

    override val clientBuilder: OkHttpClient.Builder
        get() = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)

    override val api: UsersApi
        get() = retrofit.create(UsersApi::class.java)

    private val headerInterceptor: Interceptor
        get() = Interceptor { chain ->
            Log.d(TAG, "UsersApiProvider#headerInterceptor called")
            val newRequestBuilder = chain.request().newBuilder()

            dataStore.getAccessTokenSync()?.let {
                if (it.isBlank()) {
                    return@let
                }

                Log.d(TAG, "accessToken = $it")
                val authHeader = Authorization(accessToken = it)
                newRequestBuilder.addHeader(authHeader.name, authHeader.value)
            }

            return@Interceptor chain.proceed(newRequestBuilder.build())
        }

    companion object {
        fun getInstance(context: Context) = UsersApiProvider(
            dataStore = AccessTokenDataStore(context)
        )
    }
}