package com.training.android.githubusersdemo.viewmodel.mainactivity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.android.githubusersdemo.model.data.repository.AccessTokenRepository
import com.training.android.githubusersdemo.model.data.source.local.UsersDatabaseProvider

class MainActivityViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(
                dao = UsersDatabaseProvider.getInstance(context).getUsersDao(),
                repository = AccessTokenRepository.getInstance(context),
            ) as T
        }

        throw IllegalStateException("Unknown ViewModel class")
    }
}