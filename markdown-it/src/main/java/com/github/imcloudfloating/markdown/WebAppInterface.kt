package com.github.imcloudfloating.markdown

import android.content.Context
import android.content.res.Configuration
import android.webkit.JavascriptInterface

class WebAppInterface(context: Context, private val fitSystemTheme: Boolean) {
    private val isDarkMode = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    @JavascriptInterface
    fun isDarkTheme(): Boolean {
        return if (fitSystemTheme) {
            isDarkMode
        } else {
            false
        }
    }
}