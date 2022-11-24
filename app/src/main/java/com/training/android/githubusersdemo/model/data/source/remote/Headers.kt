package com.training.android.githubusersdemo.model.data.source.remote

open class Header(
    val name: String,
    val value: String,
) {
    override fun toString(): String {
        return "$name: $value"
    }
}

class Authorization(accessToken: String) : Header("Authorization", "token $accessToken")

class Accept(value: String) : Header("Accept", value)