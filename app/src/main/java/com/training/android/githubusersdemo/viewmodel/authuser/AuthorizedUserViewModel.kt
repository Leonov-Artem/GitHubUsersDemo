package com.training.android.githubusersdemo.viewmodel.authuser

import androidx.lifecycle.*
import com.training.android.githubusersdemo.helper.LoadPriority
import com.training.android.githubusersdemo.helper.Result
import com.training.android.githubusersdemo.model.data.repository.AccessTokenRepository
import com.training.android.githubusersdemo.model.data.repository.AuthorizedUserRepository
import kotlinx.coroutines.launch

class AuthorizedUserViewModel(
    private val authorizedUserRepository: AuthorizedUserRepository,
    private val accessTokenRepository: AccessTokenRepository,
) : ViewModel() {

    private val priorityMutableLiveData = MutableLiveData(LoadPriority.CACHE)

    private fun triggerLoad(priority: LoadPriority) {
        priorityMutableLiveData.value = priority
    }

    fun loadUser() {
        triggerLoad(LoadPriority.CACHE)
    }

    fun refreshUser() {
        triggerLoad(LoadPriority.BACKEND)
    }

    val authorizedUserLiveData: LiveData<Result<*>> =
        Transformations.switchMap(priorityMutableLiveData) { priority ->
            authorizedUserRepository.getAuthorizedUser(priority)
        }

    fun signOut() = viewModelScope.launch {
        accessTokenRepository.removeAccessToken()
        authorizedUserRepository.removeAuthorizedUser()
    }
}