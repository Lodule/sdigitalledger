package com.sdigitalledger.utils

import android.content.Context
import android.content.SharedPreferences

class PremiumManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("digital_ledger_prefs", Context.MODE_PRIVATE)

    var isPremium: Boolean
        get() = prefs.getBoolean(KEY_IS_PREMIUM, false)
        set(value) {
            prefs.edit().putBoolean(KEY_IS_PREMIUM, value).apply()
        }

    fun grantPremiumAccess() {
        isPremium = true
    }

    fun revokePremiumAccess() {
        isPremium = false
    }

    companion object {
        private const val KEY_IS_PREMIUM = "key_is_premium"
        const val PREMIUM_PRICE_UGX = 5000
    }
}
