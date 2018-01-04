package com.github.luoyemyy.qrcode.camera

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.TextureView
import android.widget.ImageView
import com.github.luoyemyy.framework.mvp.MvpActivity
import com.github.luoyemyy.qrcode.R

class ScanActivity : MvpActivity(), IScanMvp.IScanView, TextureView.SurfaceTextureListener {

    private lateinit var mPresenter: IScanMvp.IScanPresenter
    private lateinit var mTextureView: TextureView
    private lateinit var mScanView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qrcode_activity_scan)
        initViewAndPresenter()
    }

    override fun initViewAndPresenter() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mScanView = findViewById(R.id.imgScan)

        mTextureView = findViewById(R.id.textureView)
        mTextureView.surfaceTextureListener = this

        mPresenter = ScanPresenterImpl(this, this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }


    override fun getSurface(): SurfaceTexture = mTextureView.surfaceTexture

    override fun getBitmap(): Bitmap? {
        val bitmap = mTextureView.bitmap
        if (bitmap != null) {
            return Bitmap.createBitmap(bitmap, mScanView.left, mScanView.top, mScanView.width, mScanView.height)
        }
        return null
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = true

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        mPresenter.open()
    }

    override fun finish() {
        mPresenter.close()
        super.finish()
    }
}