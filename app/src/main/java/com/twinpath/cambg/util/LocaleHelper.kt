package com.twinpath.cambg.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.twinpath.cambg.model.AppLanguage

object LocaleHelper {
    /**
     * Mengubah bahasa aplikasi secara dinamis menggunakan AppCompatDelegate
     * yang kompatibel dengan fitur Per-App Language Preferences Android 13+.
     */
    fun applyLanguage(context: Context, language: AppLanguage) {
        val localeList = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
