package com.training.android.githubusersdemo.viewmodel.mainactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.training.android.githubusersdemo.model.data.repository.AccessTokenRepository
import com.training.android.githubusersdemo.model.data.source.local.UsersDao
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val dao: UsersDao,
    private val repository: AccessTokenRepository,
) : ViewModel() {

    fun clearAuthorizedUserCache() = viewModelScope.launch {
        dao.removeAuthorizedUser()
        repository.removeAccessToken()
    }
}