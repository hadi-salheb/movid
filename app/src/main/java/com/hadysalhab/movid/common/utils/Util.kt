package com.hadysalhab.movid.common.utils

import android.content.Context
import android.util.DisplayMetrics

fun convertDpToPixel(dp: Int, context: Context): Int {
    return dp * (context.resources
        .displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}