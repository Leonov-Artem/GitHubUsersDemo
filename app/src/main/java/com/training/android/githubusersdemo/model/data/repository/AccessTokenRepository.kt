package com.training.android.githubusersdemo.model.data.repository

import android.content.Context
import android.util.Log
import com.training.android.githubusersdemo.helper.AccessTokenDataStore
import com.training.android.githubusersdemo.helper.Result
import com.training.android.githubusersdemo.model.data.source.remote.OAuthApi
import com.training.android.githubusersdemo.model.data.source.remote.providers.OAuthApiProvider
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

private val TAG = AccessTokenRepository::class.java.simpleName

class AccessTokenRepository(
    private val oAuthApi: OAuthApi,
    private val dataStore: AccessTokenDataStore,
) {

    suspend fun saveAccessToken(value: String) = dataStore.saveAccessToken(value)

    suspend fun removeAccessToken() = dataStore.removeAccessToken()

    fun requestAccessToken(code: String) = flow {
        try {
            emit(Result.loading)
            val response = oAuthApi.exchangeTemporaryCode(code)

            if (response.isSuccessful) {
                val accessToken = response.body()!!
                Log.d(TAG, "i've go accessToken: $accessToken")
                emit(Result.success(data = accessToken.value))
                return@flow
            }

            emit(Result.error(message = "hz govno kakoeto"))
        } catch (e: IOException) {
            emit(Result.error(message = "AccessTokenRepository#requestAccessToken: IOException"))
        } catch (e: HttpException) {
            emit(Result.error(message = "AccessTokenRepository#requestAccessToken: HttpException"))
        } finally {
            emit(Result.notLoading)
        }
    }

    companion object {
        fun getInstance(context: Context) = AccessTokenRepository(
            oAuthApi = OAuthApiProvider.api,
            dataStore = AccessTokenDataStore(context),
        )
    }
}