package com.github.imcloudfloating.markdown.demo

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

class DemoViewModel(application: Application) : AndroidViewModel(application) {
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
}