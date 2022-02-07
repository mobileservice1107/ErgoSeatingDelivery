package com.ms.ergoseatingdelivery

import android.content.Context
import android.content.SharedPreferences

class Preferences (context: Context)
{
    companion object {
        private const val APP_PREF = "APP_PREF"
        private const val LOGGED_IN = "LOGGED_IN"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)

    var token: String?
        get() = preferences.getString(ACCESS_TOKEN, "")
        set(value) = preferences.edit().putString(ACCESS_TOKEN, value).apply()
    var loggedIn: Boolean
        get() = preferences.getBoolean(LOGGED_IN, false)
        set(value) = preferences.edit().putBoolean(LOGGED_IN, value).apply()

}