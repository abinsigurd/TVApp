package com.alvinwijaya.tvapp.ui.detail

import androidx.core.text.HtmlCompat

internal fun String.toPlainText(): String {
    return HtmlCompat.fromHtml(
        this,
        HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString().trim()
}