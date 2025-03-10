package com.arkurl.eventtimepiece.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

object AppUtils {
    fun getVersionName(context: Context): String {
        return try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "Unknown"
        }
    }

    fun getVersionCode(context: Context): Long {
        return try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.longVersionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }

    fun getProjectLinkHtml(): String {
        return "<b><a href=\"https://github.com/arkurl/EventTimepiece\">Github</a></b>"
    }
}