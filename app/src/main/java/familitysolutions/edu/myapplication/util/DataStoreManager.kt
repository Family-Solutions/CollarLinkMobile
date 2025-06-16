package familitysolutions.edu.myapplication.util

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStoreManager {
    private val Context.dataStore by preferencesDataStore(name = "auth_prefs")
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val USERNAME_KEY = stringPreferencesKey("username")

    fun getToken(context: Context): Flow<String?> =
        context.dataStore.data.map { it[TOKEN_KEY] }

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    fun getUsername(context: Context): Flow<String?> =
        context.dataStore.data.map { it[USERNAME_KEY] }

    suspend fun saveUsername(context: Context, username: String) {
        context.dataStore.edit { it[USERNAME_KEY] = username }
    }
} 