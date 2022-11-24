package com.training.android.githubusersdemo.viewmodel.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.training.android.githubusersdemo.helper.Result
import com.training.android.githubusersdemo.model.data.repository.AccessTokenRepository
import com.training.android.githubusersdemo.model.data.repository.AuthorizedUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WebViewFragmentViewModel(
    private val accessTokenRepository: AccessTokenRepository,
    private val authorizedUserRepository: AuthorizedUserRepository,
) : ViewModel() {

    fun loadAuthorizedUserInfo(accessToken: String) = viewModelScope.launch {
        accessTokenRepository.saveAccessToken(accessToken)
        authorizedUserRepository.loadAuthorizedUserSilent()
    }

    fun requestAccessToken(code: String): Flow<Result<String>> =
        accessTokenRepository.requestAccessToken(code)
}