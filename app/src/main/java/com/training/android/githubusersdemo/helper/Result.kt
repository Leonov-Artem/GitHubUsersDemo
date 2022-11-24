package com.training.android.githubusersdemo.helper

import com.training.android.githubusersdemo.model.data.source.remote.GitHubApi
import retrofit2.Response

enum class Status {
    LOADING,
    SUCCESS,
    DEFAULT_REQUEST_LIMIT_EXCEEDED,
    AUTHORIZED_USER_REQUEST_LIMIT_EXCEEDED,
    NO_INTERNET_CONNECTION,
    ACCESS_TOKEN_IS_INVALID,
    ERROR,
    NOT_LOADING,
}

class Result<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
) {
    companion object {
        val loading = Result(Status.LOADING, null, null)

        fun <T> success(data: T) = Result(Status.SUCCESS, data, null)

        fun error(message: String) = Result(Status.ERROR, null, message)

        fun defaultRequestLimitExceeded(resetEpochSecond: Int) =
            Result(Status.DEFAULT_REQUEST_LIMIT_EXCEEDED, resetEpochSecond, null)

        fun authorizedUserRequestLimitExceeded(resetEpochSecond: Int) =
            Result(Status.AUTHORIZED_USER_REQUEST_LIMIT_EXCEEDED, resetEpochSecond, null)

        val accessTokenIsInvalid = Result(Status.ACCESS_TOKEN_IS_INVALID, null, null)

        val noInternetConnection = Result(Status.NO_INTERNET_CONNECTION, null, null)

        val notLoading = Result(Status.NOT_LOADING, null, null)
    }
}

fun handleRequestsLimitExceededState(response: Response<*>): Result<Int> {
    return when (response.requestsLimit) {
        GitHubApi.DEFAULT_REQUEST_LIMIT -> {
            Result.defaultRequestLimitExceeded(response.resetEpochSecond)
        }
        GitHubApi.AUTHORIZED_USER_REQUEST_LIMIT -> {
            Result.authorizedUserRequestLimitExceeded(response.resetEpochSecond)
        }
        else -> {
            Result.error(message = "Unhandled request limit: ${response.requestsLimit}")
        }
    }
}