package com.training.android.githubusersdemo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = UserListItem.TABLE_NAME)
data class UserListItem(
    @PrimaryKey @SerializedName("id") var id: Int,
    @SerializedName("login") var login: String,
    @SerializedName("avatar_url") var avatarUrl: String,
    @SerializedName("name") var name: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("followers") var followersCount: Int?,
    @SerializedName("following") var followingCount: Int?,
    @SerializedName("created_at") override var createdAt: String?,
    var lastUpdate: String?,
) : UserBase(createdAt) {

    companion object {
        const val TABLE_NAME = "users_table"
    }

    object Columns {
        const val ID = "id"
        const val LOGIN = "login"
    }
}