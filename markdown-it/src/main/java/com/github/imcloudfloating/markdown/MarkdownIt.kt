package com.github.imcloudfloating.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.webkit.*
import android.widget.FrameLayout
import org.apache.commons.text.StringEscapeUtils

/**
 * Markdown View
 *
 * Based on [FrameLayout] and [WebView]
 *
 * @author Cloud Li
 */
@SuppressLint("SetJavaScriptEnabled")
class MarkdownIt(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    companion object {
        private const val TAG = "MARKDOWN_IT_ANDROID"
    }

    private lateinit var webView: WebView
    private var loaded = false

    var fitSystemTheme = true

    /**
     * Required [fitSystemTheme] = false
     */
    var darkTheme: Boolean = false
        set(value) {
            field = value
            if (field) useDarkTheme() else useLightTheme()
        }
    var markdownString = String()
        set(value) {
            field = StringEscapeUtils.escapeEcmaScript(value)
            renderContent()
        }

    var urlClickListener: ((Uri?) -> Any)? = null

    /**
     * Client for WebView
     */
    private val client = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            loaded = true
            if (darkTheme) useDarkTheme() else useLightTheme()
            renderContent()
            webView.visibility = VISIBLE
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (urlClickListener == null) {
                // Open link in external browser.
                val intent = Intent(Intent.ACTION_VIEW, request?.url)
                context.startActivity(intent)
            } else {
                urlClickListener?.invoke(request?.url)
            }

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
        if (!isInEditMode) {
            inflate(context, R.layout.layout_markdown, this)
            getAttrs(attrs)
            webView = findViewById(R.id.web_view)
            webView.visibility = INVISIBLE
            webView.loadUrl("file:///android_asset/markdown-it/index.html")
            webView.addJavascriptInterface(WebAppInterface(context, fitSystemTheme), "android")
            webView.settings.run {
                javaScriptEnabled = true
                blockNetworkImage = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webView.webViewClient = client
        }
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarkdownIt)
        fitSystemTheme = typedArray.getBoolean(R.styleable.MarkdownIt_fitSystemTheme, true)
        darkTheme = typedArray.getBoolean(R.styleable.MarkdownIt_darkTheme, false)
        markdownString = typedArray.getString(R.styleable.MarkdownIt_markdownString).toString()
        typedArray.recycle()
    }

    private fun useDarkTheme() {
        if (!fitSystemTheme)
            execJavascript("javascript:useDarkTheme()")
    }

    private fun useLightTheme() {
        if (!fitSystemTheme)
            execJavascript("javascript:useLightTheme()")
    }

    /**
     * Render markdown string
     */
    private fun renderContent() {
        execJavascript("javascript:setContent('${markdownString}')")
    }

    private fun execJavascript(script: String) {
        if (loaded) {
            webView.evaluateJavascript(script, null)
            Log.d(TAG, "Render")
        }
    }
}