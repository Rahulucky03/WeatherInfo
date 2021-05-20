package com.weather.info.base.progress

import android.view.View
import android.widget.ProgressBar

class ProgressBarManager(private val progressBar: ProgressBar?) {
    fun showLoading(isVisible: Boolean) {
        progressBar?.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}