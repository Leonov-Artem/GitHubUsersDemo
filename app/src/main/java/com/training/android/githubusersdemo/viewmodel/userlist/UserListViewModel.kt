package com.training.android.githubusersdemo.viewmodel.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.training.android.githubusersdemo.model.data.repository.UserListRemoteMediator
import com.training.android.githubusersdemo.model.data.source.local.UsersDao
import com.training.android.githubusersdemo.model.entity.UserListItem
import kotlinx.coroutines.flow.Flow

private const val DEFAULT_PAGE_SIZE = 30

class UserListViewModel(
    private val dao: UsersDao,
    remoteMediator: UserListRemoteMediator,
) : ViewModel() {

    @OptIn(ExperimentalPagingApi::class)
    private val pager = Pager(
        config = PagingConfig(
            pageSize = DEFAULT_PAGE_SIZE,
            enablePlaceholders = true,
            maxSize = DEFAULT_PAGE_SIZE * 3,
        ),
        remoteMediator = remoteMediator,
        pagingSourceFactory = { dao.getUsers() },
    )

    val users: Flow<PagingData<UserListItem>> = pager.flow.cachedIn(viewModelScope)

    suspend fun isUserAuthorized() = dao.getAuthorizedUser() != null
}
