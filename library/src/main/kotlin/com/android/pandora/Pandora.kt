package com.android.pandora

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.android.pandora.compress.Luban
import com.android.pandora.compress.OnCompressListener
import com.android.pandora.display.ClayAdapter
import com.android.pandora.display.ClaysAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.lib_pandora_activity_pandora.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tangchao on 2017/10/10.
 *
 * Pandora 入口类
 */
class Pandora : AppCompatActivity() {

    private val _requestCodeShot = 1000
    private val _requestCodePermissionCamera = 2000
    private val _requestCodePermissionReadExternalStorage = 3000
    private val clayList = mutableListOf<Clay>()
    private val _timeFormat = "yyyyMMdd_HHmmss"

    private var currentId: Long = 0L

    private lateinit var clayAdapter: ClayAdapter
    private lateinit var claysAdapter: ClaysAdapter
    /**
     * 原图路径
     */
    private lateinit var originalPath: String
    /**
     * 压缩图路径
     */
    private lateinit var compressedPath: String

    companion object {

        private val MaxCountKey = "count"

        private var onHookListener: OnHookListener? = null
        private var onHook: ((MutableList<String>) -> Unit)? = null
        private var onError: ((Throwable?) -> Unit)? = null
        private var onCancel: (() -> Unit)? = null

        @JvmStatic
        fun open(context: Context, maxCount: Int = 1, onHookListener: OnHookListener) {
            context.startActivity(Intent(context, Pandora::class.java).apply {
                putExtras(Bundle().apply {
                    putInt(MaxCountKey, maxCount)
                })
            })
            Companion.onHookListener = onHookListener
        }

        @JvmStatic
        fun open(context: Context, maxCount: Int = 1,
                 onHook: ((MutableList<String>) -> Unit), onError: ((Throwable?) -> Unit)? = null, onCancel: (() -> Unit)? = null) {
            context.startActivity(Intent(context, Pandora::class.java).apply {
                putExtras(Bundle().apply {
                    putInt(MaxCountKey, maxCount)
                })
            })
            Companion.onHook = onHook
            Companion.onError = onError
            Companion.onCancel = onCancel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_pandora_activity_pandora)

        val max = intent.extras?.getInt(MaxCountKey) ?: 0

        with(lib_pandora_activity_pandora_clay_recycler_view) {
            layoutManager = GridLayoutManager(this@Pandora, countInLine)
            clayAdapter = ClayAdapter(this@Pandora, mutableListOf(), max, {
                clayList.remove(it)
            }, {
                clayList.add(it)
            }, {
                if (0 == it) {
                    lib_pandora_activity_pandora_sure_button_view.visibility = View.GONE
                } else {
                    lib_pandora_activity_pandora_sure_button_view.text = String.format(Locale.CHINA, resources.getString(R.string.lib_pandora_sure), it, max)
                    lib_pandora_activity_pandora_sure_button_view.visibility = View.VISIBLE
                }
            }).apply {
                adapter = this
            }
        }
        with(lib_pandora_activity_pandora_clays_recycler_view) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@Pandora)
            claysAdapter = ClaysAdapter(this@Pandora, mutableListOf(), {
                lib_pandora_activity_pandora_album_button.text = it.name
                with(it.id) {
                    if (currentId != this) {
                        lib_pandora_activity_pandora_sure_button_view.visibility = View.GONE

                        currentId = this
                        if (0L == this) {
                            scanClayData()
                        } else {
                            updateClayData(this)
                        }
                    }
                }
                release()
            }).apply {
                adapter = this
            }
        }

        lib_pandora_activity_pandora_album_button.setOnClickListener {
            if (lib_pandora_activity_pandora_extra.isShown) {
                lib_pandora_activity_pandora_extra.visibility = View.GONE
            } else {
                lib_pandora_activity_pandora_extra.visibility = View.VISIBLE
            }
        }

        lib_pandora_activity_pandora_sure_button_view.setOnClickListener {
            val dataList = mutableListOf<String?>()
            clayList.map {
                dataList.add(it.data)
            }

            val count = dataList.size
            var size = 0

            val resultList = mutableListOf<String>()

            Luban.with(this)
                    .load(dataList)
                    .setCompressListener(object : OnCompressListener {
                        override fun onStart() {
                            lib_pandora_activity_progress_bar.visibility = View.VISIBLE
                        }

                        override fun onSuccess(file: File) {
                            lib_pandora_activity_progress_bar.visibility = View.GONE
                            size++

                            resultList.add(file.absolutePath)

                            if (size >= count) {
                                onHookListener?.onHook(resultList)
                                onHook?.invoke(resultList)
                                finish()
                            }
                        }

                        override fun onError(e: Throwable) {
                            lib_pandora_activity_progress_bar.visibility = View.GONE
                            onHookListener?.onError(e)
                            onError?.invoke(e)
                            finish()
                        }
                    })
                    .launch()
        }

        lib_pandora_activity_pandora_navigation_icon_view.setOnClickListener {
            onBackPressed()
        }

        lib_pandora_activity_pandora_shot_button_view.setOnClickListener {
            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
                Toast.makeText(this, R.string.lib_pandora_check_camera, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            initShotPath()

            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), _requestCodePermissionCamera)
                    } else {
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), _requestCodePermissionCamera)
                    }
                } else {
                    openCamera()
                }
            } else {
                openCamera()
            }
        }

        lib_pandora_activity_progress_bar.setOnClickListener {

        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), _requestCodePermissionReadExternalStorage)
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), _requestCodePermissionReadExternalStorage)
                }
            } else {
                scanClaysData()
                scanClayData()
            }
        } else {
            scanClaysData()
            scanClayData()
        }
    }

    override fun onDestroy() {
        onHookListener = null
        onHook = null
        onError = null
        onCancel = null
        clayList.clear()
        super.onDestroy()
    }

    private fun openCamera() {
        val file = File(originalPath)

        val uri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(this, "$hostPackageName.FileProvider", file)
        } else {
            Uri.fromFile(file)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
            flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        startActivityForResult(intent, _requestCodeShot)
    }

    /**
     * 初始化拍照路径
     */
    private fun initShotPath() {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val timeTip = System.currentTimeMillis().let {
                SimpleDateFormat(_timeFormat, Locale.CHINA).format(it)
            }
            val parentFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/$hostSubName").also {
                if (!it.exists()) {
                    it.mkdirs()
                }
            }
            originalPath = "$parentFile/IMG_$timeTip.jpg"
            compressedPath = getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath ?: ""
        } else {
            Toast.makeText(this, R.string.lib_pandora_check_external_storage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun scanClaysData() {
        Observable.create<MutableList<Clay>> {
            val dataList = Provider.getInstance(this)?.obtainAlbumList()
            it.onNext(dataList ?: mutableListOf())
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            claysAdapter.update(it)
        }
    }

    private fun scanClayData() {
        Observable.create<MutableList<Clay>> {
            val dataList = Provider.getInstance(this)?.obtainAllList()
            it.onNext(dataList ?: mutableListOf())
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            clayAdapter.update(it)
        }
    }

    private fun updateClayData(parentId: Long) {
        Observable.create<MutableList<Clay>> {
            val dataList = Provider.getInstance(this)?.obtainPandoraWithAlbumName(parentId)
            it.onNext(dataList ?: mutableListOf())
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            clayAdapter.update(it)
        }
    }

    override fun onBackPressed() {
        release()
    }

    private fun release() {
        if (null != lib_pandora_activity_pandora_extra && lib_pandora_activity_pandora_extra.isShown) {
            lib_pandora_activity_pandora_extra.visibility = View.GONE
        } else {
            if (null != lib_pandora_activity_progress_bar && !lib_pandora_activity_progress_bar.isShown) {
                onHookListener?.onCancel()
                onCancel?.invoke()
                super.onBackPressed()
            }
        }
    }

    fun onEmptyClick(v: View) {
        release()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
            if (_requestCodePermissionCamera == requestCode) {
                openCamera()
            } else if (_requestCodePermissionReadExternalStorage == requestCode) {
                scanClayData()
                scanClaysData()
            }
        } else {
            if (_requestCodePermissionCamera == requestCode) {

            } else if (_requestCodePermissionReadExternalStorage == requestCode) {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (_requestCodeShot == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                Luban.with(this)
                        .load(originalPath)
                        .setTargetDir(compressedPath)
                        .setCompressListener(object : OnCompressListener {
                            override fun onStart() {
                                lib_pandora_activity_progress_bar.visibility = View.VISIBLE
                            }

                            override fun onSuccess(file: File) {
                                lib_pandora_activity_progress_bar.visibility = View.GONE
                                val dataList = mutableListOf(file.absolutePath)
                                onHookListener?.onHook(dataList)
                                onHook?.invoke(dataList)
                                finish()
                            }

                            override fun onError(e: Throwable) {
                                lib_pandora_activity_progress_bar.visibility = View.GONE
                                onHookListener?.onError(e)
                                onError?.invoke(e)
                                finish()
                            }
                        })
                        .launch()
            }
        }
    }
}