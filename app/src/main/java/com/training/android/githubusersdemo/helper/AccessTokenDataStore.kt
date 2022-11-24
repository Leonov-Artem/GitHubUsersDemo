package com.training.android.githubusersdemo.helper

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token_key")

class AccessTokenDataStore(
    context: Context,
) {

    private val dataStore = context.dataStore

    val accessTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    fun getAccessTokenSync(): String? {
        val preferences = runBlocking { dataStore.data.firstOrNull() }
        preferences?.let { return it[ACCESS_TOKEN_KEY] }
        return null
    }

    suspend fun saveAccessToken(value: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = value
        }
    }

    suspend fun removeAccessToken() {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = ""
        }
    }
}