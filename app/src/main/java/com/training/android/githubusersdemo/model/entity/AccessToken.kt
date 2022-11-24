package com.training.android.githubusersdemo.model.entity

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("access_token") val value: String,
    @SerializedName("scope") val scopes: String,
) {

}
