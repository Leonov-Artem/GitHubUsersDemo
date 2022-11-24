package com.training.android.githubusersdemo.model.data.source.local

import androidx.paging.PagingSource
import androidx.room.*
import com.training.android.githubusersdemo.model.entity.AuthorizedUser
import com.training.android.githubusersdemo.model.entity.UserListItem

@Dao
interface UsersDao {

    //--------------------CREATE--------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<UserListItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: AuthorizedUser)

    //--------------------READ--------------------

    @Query("SELECT * FROM ${UserListItem.TABLE_NAME}")
    fun getUsers(): PagingSource<Int, UserListItem>

    @Query("SELECT * FROM ${UserListItem.TABLE_NAME} WHERE ${UserListItem.Columns.LOGIN} = :login")
    suspend fun getUserByLogin(login: String): UserListItem

    @Query("SELECT MAX(${UserListItem.Columns.ID}) FROM ${UserListItem.TABLE_NAME}")
    suspend fun getMaxId(): Int

    @Query("SELECT * FROM ${AuthorizedUser.TABLE_NAME}")
    suspend fun getAuthorizedUser(): AuthorizedUser?

    //--------------------UPDATE--------------------

    @Update
    suspend fun update(user: UserListItem)

    @Update
    suspend fun update(user: AuthorizedUser)

    //--------------------DELETE--------------------

    @Query("DELETE FROM ${UserListItem.TABLE_NAME}")
    suspend fun removeUsers(): Int

    @Query("DELETE FROM ${AuthorizedUser.TABLE_NAME}")
    suspend fun removeAuthorizedUser(): Int
}