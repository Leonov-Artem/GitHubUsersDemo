package com.training.android.githubusersdemo.model.data.source.remote

import com.training.android.githubusersdemo.model.entity.AccessToken
import com.training.android.githubusersdemo.model.entity.AuthorizedUser
import com.training.android.githubusersdemo.model.entity.UserListItem
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Header
import retrofit2.http.Query

interface GitHubApi {
    companion object {
        const val DEFAULT_REQUEST_LIMIT = 60
        const val AUTHORIZED_USER_REQUEST_LIMIT = 5000
    }
}

interface UsersApi : GitHubApi {

    @GET("users?")
    suspend fun getUsers(
        @Query("since") after: Int?,
        @Query("per_page") amount: Int,
    ): Response<List<UserListItem>>

    @GET("users/{login}")
    suspend fun getUserDetails(
        @Header("If-Modified-Since") ifModifiedSince: String?,
        @Path("login") login: String,
    ): Response<UserListItem>

    @GET("user")
    suspend fun getAuthorizedUser(
        @Header("If-Modified-Since") ifModifiedSince: String? = null,
    ): Response<AuthorizedUser>
}

interface OAuthApi : GitHubApi {

    @POST("login/oauth/access_token?")
    suspend fun exchangeTemporaryCode(
        @Query("code") code: String,
    ): Response<AccessToken>
}

interface TokenValidityApi : GitHubApi {

    @POST("applications/$CLIENT_ID_PATH_SEGMENT/token")
    suspend fun checkAccessTokenValidity(
        @Body accessToken: AccessToken,
    ): Response<AccessToken>

    @GET("/")
    suspend fun checkAccessTokenValidity(
        @Header("Authorization") authHeaderValue: String,
    ): Response<*>

    companion object {
        const val CLIENT_ID_PATH_SEGMENT = "{client_id}"
    }
}