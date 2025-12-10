package com.example.flightdeck.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * Utility functions for network connectivity
 */
object NetworkUtils {

    /**
     * Check if device has active internet connection
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo != null && networkInfo.isConnected
        }
    }

    /**
     * Get user-friendly error message for network errors
     */
    fun getNetworkErrorMessage(error: Throwable): String {
        return when {
            error is java.net.UnknownHostException ->
                "No internet connection. Please check your network settings."
            error is java.net.SocketTimeoutException ->
                "Connection timeout. Please try again."
            error is javax.net.ssl.SSLException ->
                "Secure connection failed. Please check your device's date and time settings."
            error.message?.contains("timeout", ignoreCase = true) == true ->
                "Request timed out. Please try again."
            error.message?.contains("network", ignoreCase = true) == true ->
                "Network error. Please check your connection."
            else ->
                "Connection error: ${error.message ?: "Unknown error"}"
        }
    }

    /**
     * Retry logic for network operations
     */
    suspend fun <T> retryWithBackoff(
        maxRetries: Int = 3,
        initialDelayMs: Long = 1000,
        maxDelayMs: Long = 10000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMs
        repeat(maxRetries - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                // Don't retry on certain errors
                if (e is IllegalArgumentException || e is IllegalStateException) {
                    throw e
                }
            }
            kotlinx.coroutines.delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
        }
        return block() // Last attempt
    }
}
