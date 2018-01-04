package com.github.luoyemyy.qrcode.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import com.github.luoyemyy.framework.mvp.MvpPresenterImpl
import com.github.luoyemyy.framework.utils.toast
import com.github.luoyemyy.qrcode.QrCode
import com.github.luoyemyy.qrcode.core.WorkHandler

internal class ScanPresenterImpl(private val mActivity: AppCompatActivity, private val mView: IScanMvp.IScanView) : MvpPresenterImpl(mView), IScanMvp.IScanPresenter {

    private val mContext: Context = mActivity.applicationContext
    private var mCamera: Camera? = null

    private var mWorkHandler: WorkHandler? = null
    private var mMainHandler: Handler? = null

    override fun open() {
        requestPermission(1, arrayOf(Manifest.permission.CAMERA)).withPass {

            mWorkHandler = WorkHandler.start(this)
            mMainHandler = Handler()

            val numberOfCameras = Camera.getNumberOfCameras()
            val cameraInfo = Camera.CameraInfo()
            for (i in 0 until numberOfCameras) {
                Camera.getCameraInfo(i, cameraInfo)
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCamera = Camera.open(i)
                    break
                }
            }
            if (mCamera == null) {
                mContext.toast(string = "无法打开相机")
                mActivity.finish()
            }
            val params = mCamera!!.parameters
            params.sceneMode = Camera.Parameters.SCENE_MODE_BARCODE
            params.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            setCameraDisplayOrientation(mActivity, cameraInfo, mCamera!!)
            mCamera!!.setPreviewTexture(mView.getSurface())
            mCamera!!.startPreview()

            mWorkHandler?.focus()

        }.request(mActivity)
    }

    private fun setCameraDisplayOrientation(activity: Activity, info: Camera.CameraInfo, camera: Camera) {
        val rotation = activity.windowManager.defaultDisplay.rotation
        val degrees = when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
        val result = if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            val r = (info.orientation + degrees) % 360
            (360 - r) % 360                 // compensate the mirror
        } else {                            // back-facing
            (info.orientation - degrees + 360) % 360
        }
        camera.setDisplayOrientation(result)
    }

    override fun close() {
        mWorkHandler?.close()
        mWorkHandler = null
        mMainHandler = null
        mCamera?.stopPreview()
        mCamera?.release()
    }

    override fun focus() {
        mCamera?.autoFocus { success, _ ->
            if (success) {
                val msg = mWorkHandler?.obtainMessage() ?: return@autoFocus
                msg.obj = mView.getBitmap()
                msg.what = WorkHandler.PARSE
                mWorkHandler?.sendMessage(msg)
            } else {
                mWorkHandler?.focus()
            }
        }
    }

    override fun parseResult(text: String) {
        mMainHandler?.post {
            mActivity.setResult(Activity.RESULT_OK, Intent().apply { putExtra(QrCode.RESULT, text) })
            mActivity.finish()
        }
    }
}