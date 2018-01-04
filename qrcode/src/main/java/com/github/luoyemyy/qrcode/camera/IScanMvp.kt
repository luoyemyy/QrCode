package com.github.luoyemyy.qrcode.camera

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import com.github.luoyemyy.framework.mvp.IMvp
import com.github.luoyemyy.qrcode.core.FocusListener

internal interface IScanMvp {
    interface IScanPresenter : IMvp.IPresenter, FocusListener {
        fun open()
        fun close()
    }

    interface IScanView : IMvp.IView {
        fun getSurface(): SurfaceTexture
        fun getBitmap(): Bitmap?
    }
}