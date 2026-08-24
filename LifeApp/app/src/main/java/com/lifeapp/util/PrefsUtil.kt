package com.lifeapp.util

import android.content.Context
import android.content.SharedPreferences

object PrefsUtil {
    private const val PREFS_NAME = "life_app_prefs"
    private const val KEY_BACKGROUND_URI = "background_uri"
    private const val KEY_THEME_MODE = "theme_mode"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getBackgroundUri(context: Context): String? {
        return getPrefs(context).getString(KEY_BACKGROUND_URI, null)
    }

    fun setBackgroundUri(context: Context, uri: String?) {
        getPrefs(context).edit().putString(KEY_BACKGROUND_URI, uri).apply()
    }

    fun getThemeMode(context: Context): Int {
        return getPrefs(context).getInt(KEY_THEME_MODE, 0) // 0=light,1=dark,2=system
    }

    fun setThemeMode(context: Context, mode: Int) {
        getPrefs(context).edit().putInt(KEY_THEME_MODE, mode).apply()
    }
}
