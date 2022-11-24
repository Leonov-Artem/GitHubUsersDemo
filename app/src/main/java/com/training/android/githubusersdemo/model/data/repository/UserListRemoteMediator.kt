package com.training.android.githubusersdemo.model.data.repository

import android.content.Context
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.training.android.githubusersdemo.helper.*
import com.training.android.githubusersdemo.model.data.source.local.UsersDatabase
import com.training.android.githubusersdemo.model.data.source.local.UsersDatabaseProvider
import com.training.android.githubusersdemo.model.data.source.remote.GitHubApi
import com.training.android.githubusersdemo.model.data.source.remote.UsersApi
import com.training.android.githubusersdemo.model.data.source.remote.providers.UsersApiProvider
import com.training.android.githubusersdemo.model.entity.UserListItem
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

private val TAG = UserListRemoteMediator::class.java.simpleName

@OptIn(ExperimentalPagingApi::class)
class UserListRemoteMediator(
    private val database: UsersDatabase,
    private val backend: UsersApi,
    private val network: Utils.Network,
) : RemoteMediator<Int, UserListItem>() {

    private val dao = database.getUsersDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserListItem>
    ): MediatorResult {
        try {
            val maxId = when (loadType) {
                LoadType.REFRESH -> {
                    null
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    dao.getMaxId()
                }
            }

            val response = backend.getUsers(
                after = maxId,
                amount = state.config.pageSize
            )

            if (response.isSuccessful) {
                return handleSuccessState(loadType, response)
            }

            if (response.isRequestsLimitExceeded) {
                return handleRequestLimitExceededState(response)
            }

            if (response.isAccessTokenInvalid) {
                return errorWithMessage(status = Status.ACCESS_TOKEN_IS_INVALID)
            }

            return errorWithMessage(message = "Unhandled response code: ${response.code()}")
        } catch (e: IOException) {
            return handleIOException(e)
        } catch (e: HttpException) {
            return handleHttpException(e)
        }
    }

    private suspend fun handleSuccessState(
        loadType: LoadType,
        response: Response<List<UserListItem>>
    ): MediatorResult {
        var users = listOf<UserListItem>()
        database.withTransaction {
            if (loadType == LoadType.REFRESH) {
                dao.removeUsers()
            }

            users = response.body()!!
            dao.insert(users)
        }
        return MediatorResult.Success(endOfPaginationReached = users.isEmpty())
    }

    private fun handleRequestLimitExceededState(response: Response<List<UserListItem>>): MediatorResult {
        return when (response.requestsLimit) {
            GitHubApi.DEFAULT_REQUEST_LIMIT -> {
                errorWithMessage(
                    Status.DEFAULT_REQUEST_LIMIT_EXCEEDED,
                    response.resetEpochSecond
                )
            }
            GitHubApi.AUTHORIZED_USER_REQUEST_LIMIT -> {
                errorWithMessage(
                    Status.AUTHORIZED_USER_REQUEST_LIMIT_EXCEEDED,
                    response.resetEpochSecond
                )
            }
            else -> {
                errorWithMessage(message = "Unhandled request limit: ${response.requestsLimit}")
            }
        }
    }

    private fun handleIOException(e: IOException): MediatorResult.Error {
        if (!network.isInternetConnected) {
            return errorWithMessage(status = Status.NO_INTERNET_CONNECTION)
        }

        Log.d(TAG, "IOException: ${e.message}")
        return MediatorResult.Error(e)
    }

    private fun handleHttpException(e: HttpException): MediatorResult.Error {
        Log.d(TAG, "HttpException: ${e.message}")
        return MediatorResult.Error(e)
    }

    companion object {
        fun getInstance(context: Context) = UserListRemoteMediator(
            database = UsersDatabaseProvider.getInstance(context),
            backend = UsersApiProvider.getInstance(context).api,
            network = Utils.Network.getInstance(),
        )

        private fun errorWithMessage(message: String): MediatorResult.Error {
            return MediatorResult.Error(Throwable(message = message))
        }

        private fun errorWithMessage(status: Status): MediatorResult.Error {
            return errorWithMessage(status.name)
        }

        private fun errorWithMessage(status: Status, resetEpochSecond: Int): MediatorResult.Error {
            return errorWithMessage(message = "${status.name} $resetEpochSecond")
        }
    }
}