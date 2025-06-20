package familitysolutions.edu.myapplication.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val USERNAME_KEY = stringPreferencesKey("username")

    val getToken: Flow<String?> =
        context.dataStore.data.map { it[TOKEN_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    val getUsername: Flow<String?> =
        context.dataStore.data.map { it[USERNAME_KEY] }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { it[USERNAME_KEY] = username }
    }
} 