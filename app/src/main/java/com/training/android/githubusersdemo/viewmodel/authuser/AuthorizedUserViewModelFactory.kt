package com.training.android.githubusersdemo.viewmodel.authuser

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.android.githubusersdemo.model.data.repository.AccessTokenRepository
import com.training.android.githubusersdemo.model.data.repository.AuthorizedUserRepository

class AuthorizedUserViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthorizedUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthorizedUserViewModel(
                authorizedUserRepository = AuthorizedUserRepository.getInstance(context),
                accessTokenRepository = AccessTokenRepository.getInstance(context),
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}