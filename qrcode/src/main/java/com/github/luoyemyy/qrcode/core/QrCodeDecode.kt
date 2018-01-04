package com.github.luoyemyy.qrcode.core

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader


object QrCodeDecode {

    fun decode(data: ByteArray, w: Int, h: Int): String {
        val luminanceSource = PlanarYUVLuminanceSource(data, w, h, 0, 0, w, h, false)
        val result = QRCodeReader().decode(BinaryBitmap(HybridBinarizer(luminanceSource)))
        return result.text
    }

    fun decode(data: IntArray, w: Int, h: Int): String {
        val luminanceSource = RGBLuminanceSource(w, h, data)
        val result = QRCodeReader().decode(BinaryBitmap(HybridBinarizer(luminanceSource)))
        return result.text
    }

    fun decode(bitmap: Bitmap): String {
        val array = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(array, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return decode(array, bitmap.width, bitmap.height)
    }
}