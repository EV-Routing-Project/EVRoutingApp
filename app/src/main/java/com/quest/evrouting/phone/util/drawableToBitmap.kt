package com.quest.evrouting.phone.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap

fun drawableToBitmap(
    context: Context,
    @DrawableRes resId: Int
): Bitmap {
    val drawable = ContextCompat.getDrawable(context, resId)
        ?: error("Drawable not found")

    val bitmap = createBitmap(
        drawable.intrinsicWidth.coerceAtLeast(1),
        drawable.intrinsicHeight.coerceAtLeast(1)
    )

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}
