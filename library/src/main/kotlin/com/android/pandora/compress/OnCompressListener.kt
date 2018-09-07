package com.android.pandora.compress

import java.io.File

/**
 * Created by tangchao on 2017/11/1.
 */
internal interface OnCompressListener {
    fun onStart()
    fun onSuccess(file: File)
    fun onError(e: Throwable)
}