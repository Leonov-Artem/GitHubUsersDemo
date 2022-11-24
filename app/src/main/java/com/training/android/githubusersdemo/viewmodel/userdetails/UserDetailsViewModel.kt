package com.training.android.githubusersdemo.viewmodel.userdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.training.android.githubusersdemo.helper.LoadPriority
import com.training.android.githubusersdemo.helper.Result
import com.training.android.githubusersdemo.model.data.repository.UserDetailsRepository

private val TAG = UserDetailsViewModel::class.java.simpleName

class UserDetailsViewModel(
    private val login: String,
    private val backend: UserDetailsRepository,
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

    val userLiveData: LiveData<Result<*>> =
        Transformations.switchMap(priorityMutableLiveData) { priority ->
            backend.getUserDetails(login, priority)
        }
}