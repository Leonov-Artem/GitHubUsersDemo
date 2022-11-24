package com.training.android.githubusersdemo.model.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.training.android.githubusersdemo.helper.*
import com.training.android.githubusersdemo.model.data.source.local.UsersDao
import com.training.android.githubusersdemo.model.data.source.local.UsersDatabaseProvider
import com.training.android.githubusersdemo.model.data.source.remote.UsersApi
import com.training.android.githubusersdemo.model.data.source.remote.providers.UsersApiProvider
import com.training.android.githubusersdemo.model.entity.UserListItem
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

private val TAG = UserDetailsRepository::class.java.simpleName

class UserDetailsRepository(
    private val dao: UsersDao,
    private val backend: UsersApi,
    private val network: Utils.Network,
) {

    fun getUserDetails(login: String, priority: LoadPriority): LiveData<Result<*>> {
        return when (priority) {
            LoadPriority.CACHE -> {
                getUserDetailsWithCachePriority(login)
            }
            LoadPriority.BACKEND -> {
                getUserDetailsWithBackendPriority(login)
            }
        }
    }

    private fun getUserDetailsWithCachePriority(login: String) = liveData {
        val cachedUser = dao.getUserByLogin(login)
        if (cachedUser.hasDetails) {
            emit(Result.success(cachedUser))
            return@liveData
        }

        try {
            emit(Result.loading)
            requestUserDetails(cachedUser)?.let { emit(it) }
        } catch (e: IOException) {
            emit(handleIOException(e))
        } catch (e: HttpException) {
            emit(handleHttpException(e))
        } finally {
            emit(Result.notLoading)
        }
    }

    private fun getUserDetailsWithBackendPriority(login: String) = liveData {
        val cachedUser = dao.getUserByLogin(login)

        try {
            emit(Result.loading)
            requestUserDetails(cachedUser)?.let { result ->
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

        if (cachedUser.hasDetails) {
            emit(Result.success(cachedUser))
        }
    }

    private suspend fun requestUserDetails(cachedUser: UserListItem): Result<*>? {
        val response = backend.getUserDetails(
            ifModifiedSince = cachedUser.lastUpdate,
            login = cachedUser.login,
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

    private suspend fun handleSuccessState(response: Response<UserListItem>): Result<UserListItem> {
        response.body()!!.apply {
            this.lastUpdate = response.lastUpdateDatetime
            dao.update(user = this)
            return Result.success(data = this)
        }
    }

    private fun handleIOException(e: IOException): Result<UserListItem> {
        if (!network.isInternetConnected) {
            return Result.noInternetConnection
        }

        return Result.error(message = e.localizedMessage ?: "IOException")
    }

    private fun handleHttpException(e: HttpException): Result<UserListItem> {
        return Result.error(message = e.localizedMessage ?: "HttpException:")
    }

    companion object {
        fun getInstance(context: Context) = UserDetailsRepository(
            dao = UsersDatabaseProvider.getInstance(context).getUsersDao(),
            backend = UsersApiProvider.getInstance(context).api,
            network = Utils.Network.getInstance(),
        )
    }
}