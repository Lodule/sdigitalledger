package com.sdigitalledger.analytics

import android.content.Context
import android.os.Bundle

class AnalyticsManager private constructor(context: Context) {

    private val applicationContext = context.applicationContext

    fun logEvent(eventName: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) -> putString(key, value) }
        }
        // Logs event to analytics provider (Firebase / Custom backend)
        println("[Analytics] Event logged: $eventName | Params: $params")
    }

    fun logScreenView(screenName: String) {
        logEvent("screen_view", mapOf("screen_name" to screenName))
    }

    fun logSaleRecorded(amount: Double, isCredit: Boolean) {
        logEvent("sale_recorded", mapOf(
            "amount" to amount.toString(),
            "is_credit" to isCredit.toString()
        ))
    }

    fun logPremiumUpgrade() {
        logEvent("premium_upgrade_purchased", mapOf("tier" to "freemium_to_pro"))
    }

    fun logFeedbackSubmitted(rating: Int) {
        logEvent("feedback_submitted", mapOf("rating" to rating.toString()))
    }

    companion object {
        @Volatile
        private var instance: AnalyticsManager? = null

        fun getInstance(context: Context): AnalyticsManager {
            return instance ?: synchronized(this) {
                instance ?: AnalyticsManager(context).also { instance = it }
            }
        }
    }
}
