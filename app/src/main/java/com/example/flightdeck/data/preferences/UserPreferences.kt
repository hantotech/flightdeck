package com.example.flightdeck.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightdeck.data.model.UserTier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * User preferences manager using DataStore
 * Manages AI model preferences and user tier
 */
class UserPreferences(private val context: Context) {

    companion object {
        private val USER_TIER_KEY = stringPreferencesKey("user_tier")
        private val PREFERRED_PROVIDER_KEY = stringPreferencesKey("preferred_provider")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val IS_PREMIUM_KEY = stringPreferencesKey("is_premium")

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    /**
     * Get user tier as Flow
     */
    val userTier: Flow<UserTier> = context.dataStore.data.map { preferences ->
        val tierString = preferences[USER_TIER_KEY] ?: UserTier.BASIC.name
        try {
            UserTier.valueOf(tierString)
        } catch (e: Exception) {
            UserTier.BASIC
        }
    }

    /**
     * Get preferred provider as Flow
     */
    val preferredProvider: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PREFERRED_PROVIDER_KEY]
    }

    /**
     * Get user ID as Flow
     */
    val userId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    /**
     * Get premium status as Flow
     */
    val isPremium: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_PREMIUM_KEY]?.toBoolean() ?: false
    }

    /**
     * Set user tier
     */
    suspend fun setUserTier(tier: UserTier) {
        context.dataStore.edit { preferences ->
            preferences[USER_TIER_KEY] = tier.name
        }
    }

    /**
     * Set preferred provider
     * @param provider One of: "claude-sonnet", "claude-haiku", "gemini-flash", "gemini-pro"
     */
    suspend fun setPreferredProvider(provider: String?) {
        context.dataStore.edit { preferences ->
            if (provider != null) {
                preferences[PREFERRED_PROVIDER_KEY] = provider
            } else {
                preferences.remove(PREFERRED_PROVIDER_KEY)
            }
        }
    }

    /**
     * Set user ID
     */
    suspend fun setUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    /**
     * Set premium status
     */
    suspend fun setIsPremium(isPremium: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_PREMIUM_KEY] = isPremium.toString()

            // Update user tier based on premium status
            val newTier = if (isPremium) UserTier.PREMIUM else UserTier.BASIC
            preferences[USER_TIER_KEY] = newTier.name
        }
    }

    /**
     * Clear all preferences
     */
    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

/**
 * Preference configuration for AI models
 */
data class AIModelPreference(
    val userTier: UserTier,
    val preferredProvider: String?,
    val isPremium: Boolean
) {
    companion object {
        val DEFAULT = AIModelPreference(
            userTier = UserTier.BASIC,
            preferredProvider = null,
            isPremium = false
        )
    }
}
