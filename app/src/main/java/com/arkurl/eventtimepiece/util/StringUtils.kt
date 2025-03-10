package com.arkurl.eventtimepiece.util

import android.text.Spanned
import androidx.core.text.HtmlCompat

fun String.toHtml(flags: Int = HtmlCompat.FROM_HTML_MODE_LEGACY): Spanned {
    return HtmlCompat.fromHtml(this, flags)
}