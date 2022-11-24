package com.training.android.githubusersdemo.viewmodel.userdetails

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.android.githubusersdemo.model.data.repository.UserDetailsRepository

class UserDetailsViewModelFactory(
    private val context: Context,
    private val login: String,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserDetailsViewModel(
                backend = UserDetailsRepository.getInstance(context),
                login = login,
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}