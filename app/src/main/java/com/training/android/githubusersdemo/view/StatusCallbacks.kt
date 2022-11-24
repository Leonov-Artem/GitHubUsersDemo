package com.training.android.githubusersdemo.view

interface StatusCallbacks {
    fun onInvalidAccessToken()
    fun onDefaultRequestLimitExceeded(resetEpochSecond: Int)
    fun onAuthorizedUserRequestLimitExceeded(resetEpochSecond: Int)
    fun onUnavailableInternetConnection()
}