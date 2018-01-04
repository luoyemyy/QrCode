package com.github.luoyemyy.qrcode

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.github.luoyemyy.qrcode.camera.ScanActivity

class QrCode private constructor() {

    private var mResult: (text: String?) -> Unit = {}
    private var mCode = REQUEST_CODE

    fun withCode(code: Int): QrCode {
        mCode = code
        return this
    }

    fun withResult(result: (text: String?) -> Unit): QrCode {
        mResult = result
        return this
    }


    fun start(activity: Activity): QrCode {
        activity.startActivityForResult(Intent(activity, ScanActivity::class.java), mCode)
        return this
    }

    fun start(fragment: Fragment): QrCode {
        fragment.startActivityForResult(Intent(fragment.context, ScanActivity::class.java), mCode)
        return this
    }

    fun result(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCode && resultCode == Activity.RESULT_OK && data != null) {
            mResult(data.getStringExtra(RESULT))
        }
    }

    companion object {
        val REQUEST_CODE = 234
        val RESULT = "qr_code_result"

        fun build(): QrCode = QrCode()

        fun buildIntent(context: Context): Intent = Intent(context, ScanActivity::class.java)

        fun result(resultCode: Int, data: Intent?): String? {
            return if (resultCode == Activity.RESULT_OK && data != null) {
                data.getStringExtra(RESULT)
            } else null
        }
    }
}