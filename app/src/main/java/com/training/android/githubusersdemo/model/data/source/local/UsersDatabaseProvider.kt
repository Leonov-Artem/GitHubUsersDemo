package com.training.android.githubusersdemo.model.data.source.local

import android.content.Context
import androidx.room.Room

private const val DATABASE_NAME = "users_db"

object UsersDatabaseProvider {

    @Volatile
    private var instance: UsersDatabase? = null

    private fun buildUsersDatabase(context: Context) = Room.databaseBuilder(
        context.applicationContext,
        UsersDatabase::class.java,
        DATABASE_NAME,
    ).build()

    fun getInstance(context: Context): UsersDatabase {
        if (instance == null) {
            synchronized(UsersDatabase::class) {
                if (instance == null) {
                    instance = buildUsersDatabase(context)
                }
            }
        }
        return instance!!
    }
}