package com.android.pandora

import org.jetbrains.annotations.NotNull


/**
 * Created by tangchao on 2017/10/27.
 */
interface OnHookListener {

    fun onHook(@NotNull clayList: List<String>)

    fun onError(e: Throwable?)

    fun onCancel()
}
