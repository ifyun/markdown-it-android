package com.github.imcloudfloating.markdown.demo

import android.app.Application
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

class DemoViewModel(application: Application) : AndroidViewModel(application) {
    private val lightIcon =
        ResourcesCompat.getDrawable(application.resources, R.drawable.ic_light, null)
    private val nightIcon =
        ResourcesCompat.getDrawable(application.resources, R.drawable.ic_night, null)

    var fitSystemTheme = ObservableBoolean(false)
    var darkTheme = ObservableBoolean(false)
    var theme = ObservableField(nightIcon)
    var content = ObservableField<String>()

    init {
        content.set(
            BufferedReader(
                InputStreamReader(
                    application.resources.openRawResource(R.raw.demo)
                )
            ).lines().collect(Collectors.joining("\n"))
        )
    }

    fun toggleTheme() {
        darkTheme.set(!darkTheme.get())
        if (darkTheme.get()) theme.set(lightIcon) else theme.set(nightIcon)
    }
}