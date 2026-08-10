package com.rayara.sevasetu.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "rayara_seva_setu_prefs",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_RECEIPT_COLOR_MODE = "receipt_color_mode"
        const val COLOR_MODE_COLOR = "color"
        const val COLOR_MODE_BW = "bw"
    }
    
    var receiptColorMode: String
        get() = prefs.getString(KEY_RECEIPT_COLOR_MODE, COLOR_MODE_COLOR) ?: COLOR_MODE_COLOR
        set(value) = prefs.edit().putString(KEY_RECEIPT_COLOR_MODE, value).apply()
    
    fun isColorReceiptEnabled(): Boolean {
        return receiptColorMode == COLOR_MODE_COLOR
    }
}
