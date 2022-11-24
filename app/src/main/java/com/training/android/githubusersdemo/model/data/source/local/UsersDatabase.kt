package com.training.android.githubusersdemo.model.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.training.android.githubusersdemo.model.entity.AuthorizedUser
import com.training.android.githubusersdemo.model.entity.UserListItem

@Database(
    entities = [
        UserListItem::class,
        AuthorizedUser::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class UsersDatabase : RoomDatabase() {

    abstract fun getUsersDao(): UsersDao
}