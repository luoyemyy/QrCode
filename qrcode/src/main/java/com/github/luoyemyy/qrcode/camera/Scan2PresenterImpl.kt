package com.github.luoyemyy.qrcode.camera

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.*
import android.os.Build
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import com.github.luoyemyy.framework.mvp.MvpPresenterImpl
import com.github.luoyemyy.qrcode.QrCode
import com.github.luoyemyy.qrcode.core.WorkHandler


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
internal class Scan2PresenterImpl(private val mActivity: AppCompatActivity, private val mView: IScanMvp.IScanView) : MvpPresenterImpl(mView), IScanMvp.IScanPresenter {

    private val mContext: Context = mActivity.applicationContext
    private var mCamera: CameraDevice? = null
    private var mCameraSession: CameraCaptureSession? = null

    private var mWorkHandler: WorkHandler? = null
    private var mMainHandler: Handler? = null

    private lateinit var mRequestBuilder: CaptureRequest.Builder

    private val mOpenCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCamera = camera
            val surface = Surface(mView.getSurface())

            mRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                addTarget(surface)
                set(CaptureRequest.CONTROL_SCENE_MODE, CaptureRequest.CONTROL_SCENE_MODE_BARCODE)
            }
            mCamera?.createCaptureSession(listOf(surface), mConfigureCallback, null)
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
            mCamera = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
            mCamera = null
        }

        override fun onClosed(camera: CameraDevice?) {
        }

    }

    private val mConfigureCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
        }

        override fun onConfigured(session: CameraCaptureSession) {
            mCameraSession = session

            val request = mRequestBuilder.apply { set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO) }.build()
            session.setRepeatingRequest(request, null, mWorkHandler)

            mWorkHandler?.focus()
        }
    }

    override fun focus() {
        val msg = mWorkHandler?.obtainMessage() ?: return
        msg.obj = mView.getBitmap()
        msg.what = WorkHandler.PARSE
        mWorkHandler?.sendMessage(msg)
    }

    override fun parseResult(text: String) {
        mMainHandler?.post {
            mActivity.setResult(Activity.RESULT_OK, Intent().apply { putExtra(QrCode.RESULT, text) })
            mActivity.finish()
        }
    }

    override fun open() {
        mWorkHandler = WorkHandler.start(this)
        mMainHandler = Handler()

        requestPermission(1, arrayOf(Manifest.permission.CAMERA)).withPass {
            val cameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            for (cameraId in cameraManager.cameraIdList) {
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraManager.openCamera(cameraId, mOpenCallback, mWorkHandler)
                    return@withPass
                }
            }
        }.request(mActivity)
    }

    override fun close() {
        mWorkHandler?.close()
        mWorkHandler = null
        mMainHandler = null
        mCameraSession?.close()
        mCameraSession = null
        mCamera?.close()
        mCamera = null
    }
}