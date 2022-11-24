package com.training.android.githubusersdemo.viewmodel.userlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.android.githubusersdemo.model.data.repository.UserListRemoteMediator
import com.training.android.githubusersdemo.model.data.source.local.UsersDatabaseProvider

class UserListViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserListViewModel(
                dao = UsersDatabaseProvider.getInstance(context).getUsersDao(),
                remoteMediator = UserListRemoteMediator.getInstance(context),
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}