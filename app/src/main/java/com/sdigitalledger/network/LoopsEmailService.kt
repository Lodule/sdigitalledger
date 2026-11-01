package com.sdigitalledger.network

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class LoopsEmailService(private val context: Context) {

    // Loops API endpoint for email sequences & contact sync
    private val apiEndpoint = "https://api.loops.so/v1/events/send"
    private val defaultApiKey = "LOOP_API_KEY_PLACEHOLDER"

    /**
     * Triggers a Loops Email Event (e.g. Welcome Sequence, Weekly Digest, Feedback Follow-up).
     */
    suspend fun sendEmailSequence(
        userEmail: String,
        eventName: String,
        eventProperties: Map<String, String> = emptyMap()
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL(apiEndpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $defaultApiKey")
                doOutput = true
                connectTimeout = 5000
                readTimeout = 5000
            }

            val payload = buildString {
                append("{")
                append("\"email\": \"$userEmail\",")
                append("\"eventName\": \"$eventName\"")
                if (eventProperties.isNotEmpty()) {
                    append(",\"eventProperties\": {")
                    append(eventProperties.entries.joinToString(",") { "\"${it.key}\": \"${it.value}\"" })
                    append("}")
                }
                append("}")
            }

            connection.outputStream.use { it.write(payload.toByteArray(Charsets.UTF_8)) }
            val responseCode = connection.responseCode
            connection.disconnect()
            responseCode in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
