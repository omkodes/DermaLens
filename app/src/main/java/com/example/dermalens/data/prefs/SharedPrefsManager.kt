package com.example.dermalens.data.prefs

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dermalens_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CONSENT_GIVEN = "consent_given"
    }

    var isConsentGiven: Boolean
        get() = prefs.getBoolean(KEY_CONSENT_GIVEN, false)
        set(value) = prefs.edit().putBoolean(KEY_CONSENT_GIVEN, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
