package com.github.imcloudfloating.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.webkit.*
import org.apache.commons.text.StringEscapeUtils

/**
 * Markdown-It View
 *
 * Based on [WebView]
 *
 * @author Cloud Li
 */
@SuppressLint("SetJavaScriptEnabled")
class MarkdownIt(context: Context, attrs: AttributeSet) : WebView(context, attrs) {
    companion object {
        private const val TAG = "MARKDOWN_IT_ANDROID"
    }

    private var loaded = false
    var fitSystemTheme = true

    var markdownString = String()
        set(value) {
            field = StringEscapeUtils.escapeEcmaScript(value)
            renderContent()
        }

    private val client = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            loaded = true
            renderContent()
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            // Open link in external browser.
            val intent = Intent(Intent.ACTION_VIEW, request?.url)
            context.startActivity(intent)
            return true
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, error?.description.toString())
            }
        }
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)
        if (!isInEditMode) {
            loadUrl("file:///android_asset/markdown-it/index.html")
            addJavascriptInterface(WebAppInterface(context, fitSystemTheme), "android")
            settings.run {
                javaScriptEnabled = true
                blockNetworkImage = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webViewClient = client
        }
    }

    /**
     * Forced to use dark theme
     *
     * This function will set [fitSystemTheme] to false
     */
    fun setDarkTheme() {
        fitSystemTheme = false
        execJavascript("javascript:useDarkTheme()")
    }

    /**
     * Render markdown string
     */
    private fun renderContent() {
        Log.d(TAG, "render")
        execJavascript("javascript:setContent('${markdownString}')")
    }

    private fun execJavascript(script: String) {
        if (loaded) {
            evaluateJavascript(script, null)
            return
        }
    }
}