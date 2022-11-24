package com.training.android.githubusersdemo.viewmodel.signin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.android.githubusersdemo.model.data.repository.AccessTokenRepository
import com.training.android.githubusersdemo.model.data.repository.AuthorizedUserRepository

class WebViewFragmentViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WebViewFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WebViewFragmentViewModel(
                accessTokenRepository = AccessTokenRepository.getInstance(context),
                authorizedUserRepository = AuthorizedUserRepository.getInstance(context),
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}