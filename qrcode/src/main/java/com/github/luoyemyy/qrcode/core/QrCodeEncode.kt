package com.github.luoyemyy.qrcode.core

import android.content.Context
import android.graphics.Bitmap
import com.github.luoyemyy.framework.utils.dp2Px
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

object QrCodeEncode {

    fun encode(context: Context, text: String, widthDp: Int, heightDp: Int): Bitmap? {
        val w = context.dp2Px(widthDp)
        val h = context.dp2Px(heightDp)
        val result = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, w, h)
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)

        val white = context.resources.getColor(android.R.color.white)
        val black = context.resources.getColor(android.R.color.black)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) black else white
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}