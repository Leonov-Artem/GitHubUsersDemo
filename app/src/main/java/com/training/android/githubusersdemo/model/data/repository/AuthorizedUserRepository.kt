package com.training.android.githubusersdemo.model.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.training.android.githubusersdemo.helper.*
import com.training.android.githubusersdemo.model.data.source.local.UsersDao
import com.training.android.githubusersdemo.model.data.source.local.UsersDatabaseProvider
import com.training.android.githubusersdemo.model.data.source.remote.UsersApi
import com.training.android.githubusersdemo.model.data.source.remote.providers.UsersApiProvider
import com.training.android.githubusersdemo.model.entity.AuthorizedUser
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AuthorizedUserRepository(
    private val dao: UsersDao,
    private val backend: UsersApi,
    private val network: Utils.Network,
) {

    suspend fun loadAuthorizedUserSilent() {
        try {
            val response = backend.getAuthorizedUser()
            if (!response.isSuccessful) {
                return
            }

            response.body()?.let { authorizedUser ->
                authorizedUser.lastUpdate = response.lastUpdateDatetime
                dao.insert(authorizedUser)
            }
        } catch (e: Exception) {
        }
    }

    suspend fun removeAuthorizedUser() = dao.removeAuthorizedUser()

    fun getAuthorizedUser(priority: LoadPriority): LiveData<Result<*>> {
        return when (priority) {
            LoadPriority.CACHE -> {
                getAuthorizedUserWithCachePriority()
            }
            LoadPriority.BACKEND -> {
                getAuthorizedUserWithBackendPriority()
            }
        }
    }

    private fun getAuthorizedUserWithCachePriority() = liveData<Result<*>> {
        try {
            val cachedUser = dao.getAuthorizedUser()!!
            emit(Result.success(data = cachedUser))
            return@liveData
        } catch (e: Exception) {
            emit(Result.error(message = e.localizedMessage ?: "Exception"))
        }
    }

    private fun getAuthorizedUserWithBackendPriority() = liveData {
        val cachedUser = dao.getAuthorizedUser()!!

        try {
            emit(Result.loading)
            requestAuthorizedUser(cachedUser)?.let { result ->
                emit(result)
                if (result.status == Status.SUCCESS) {
                    return@liveData
                }
            }
        } catch (e: IOException) {
            emit(handleIOException(e))
        } catch (e: HttpException) {
            emit(handleHttpException(e))
        } finally {
            emit(Result.notLoading)
        }

        emit(Result.success(data = cachedUser))
    }

    private suspend fun requestAuthorizedUser(cachedUser: AuthorizedUser): Result<*>? {
        val response = backend.getAuthorizedUser(
            ifModifiedSince = cachedUser.lastUpdate,
        )

        if (response.isSuccessful) {
            return handleSuccessState(response)
        }

        if (response.isRequestsLimitExceeded) {
            return handleRequestsLimitExceededState(response)
        }

        if (response.isAccessTokenInvalid) {
            return Result.accessTokenIsInvalid
        }

        if (response.isNotModified) {
            return null
        }

        return Result.error(message = "Unhandled response code: ${response.code()}")
    }

    private suspend fun handleSuccessState(response: Response<AuthorizedUser>): Result<AuthorizedUser> {
        response.body()!!.apply {
            this.lastUpdate = response.lastUpdateDatetime
            dao.update(user = this)
            return Result.success(data = this)
        }
    }

    private fun handleIOException(e: IOException): Result<AuthorizedUser> {
        if (!network.isInternetConnected) {
            return Result.noInternetConnection
        }

        return Result.error(message = e.localizedMessage ?: "IOException")
    }

    private fun handleHttpException(e: HttpException): Result<AuthorizedUser> {
        return Result.error(message = e.localizedMessage ?: "HttpException:")
    }

    companion object {
        fun getInstance(context: Context) = AuthorizedUserRepository(
            dao = UsersDatabaseProvider.getInstance(context).getUsersDao(),
            backend = UsersApiProvider.getInstance(context).api,
            network = Utils.Network.getInstance(),
        )
    }
}