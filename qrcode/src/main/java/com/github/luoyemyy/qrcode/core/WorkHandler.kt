package com.github.luoyemyy.qrcode.core

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log

class WorkHandler(private val mLooper: Looper, private val mFocusListener: FocusListener) : Handler(mLooper) {

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            FOCUS -> {
                mFocusListener.focus()
            }
            PARSE -> {
                val bitmap = msg.obj as? Bitmap
                if (bitmap == null) {
                    focus()
                } else {
                    try {
                        val text = QrCodeDecode.decode(bitmap)
                        mFocusListener.parseResult(text)
                    } catch (throwable: Throwable) {
                        Log.i("WorkHandler", "解析失败，重新开始聚焦")
                        focus()
                    }
                }
            }
        }
    }

    fun focus() {
        sendEmptyMessageDelayed(FOCUS, 200)
    }

    fun close() {
        mLooper.quitSafely()
    }

    companion object {

        val FOCUS = 1
        val PARSE = 2

        fun start(focusListener: FocusListener): WorkHandler {
            val workerThread = HandlerThread("workerThread")
            workerThread.start()
            return WorkHandler(workerThread.looper, focusListener)
        }
    }


}