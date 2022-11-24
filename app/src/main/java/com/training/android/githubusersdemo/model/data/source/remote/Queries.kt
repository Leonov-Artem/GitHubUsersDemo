package com.training.android.githubusersdemo.model.data.source.remote

open class Query(
    val name: String,
    val value: String,
) {
    override fun toString(): String {
        return "$name=$value"
    }
}

class ClientId(value: String) : Query("client_id", value)

class ClientSecret(value: String) : Query("client_secret", value)
