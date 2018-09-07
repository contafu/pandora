package com.android.pandora

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import java.io.File

/**
 * Created by tangchao on 2017/10/19.
 */
internal class Provider {

    companion object {

        private lateinit var resolver: ContentResolver

        private var instance: Provider? = null

        @JvmStatic
        fun getInstance(context: Context): Provider? {
            resolver = context.contentResolver
            if (null == instance) {
                synchronized(Provider::class, {
                    if (null == instance) {
                        instance = Provider()
                    }
                })
            }
            return instance
        }
    }

    /**
     * 获取所有照片列表
     */
    fun obtainAllList(): MutableList<Clay> {
        val arrayList = mutableListOf<Clay>()
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.BUCKET_ID,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.DATE_ADDED,
                        MediaStore.Images.ImageColumns.DATE_MODIFIED,
                        MediaStore.Images.ImageColumns.DATE_TAKEN,
                        MediaStore.Images.ImageColumns.SIZE,
                        MediaStore.Images.ImageColumns.WIDTH,
                        MediaStore.Images.ImageColumns.HEIGHT,
                        MediaStore.Images.ImageColumns.MIME_TYPE,
                        MediaStore.Images.ImageColumns.TITLE,
                        MediaStore.Images.ImageColumns.ORIENTATION
                ),
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN
        )
        if (null != cursor && cursor.moveToNext()) {
            cursor.moveToLast()
            do {
                val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                if (File(path).exists() && 10 <= (size shr 10)) {
                    arrayList.add(Clay(
                            data = path,
                            id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)),
                            parentId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)),
                            name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)),
                            parentName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)),
                            addDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED)),
                            modifyDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED)),
                            takenDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN)),
                            title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE)),
                            mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE)),
                            size = size,
                            width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH)),
                            height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT)),
                            orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION))
                    ))
                }
            } while (cursor.moveToPrevious())
            cursor.close()
        }
        return arrayList
    }

    /**
     * 获取所有相册
     */
    fun obtainAlbumList(): MutableList<Clay> {
        val arrayList = mutableListOf<Clay>()
        val hashMap = mutableMapOf<String, Clay>()
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                        MediaStore.Images.ImageColumns.BUCKET_ID,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.SIZE
                ),
                null,
                null,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
        )
        if (null != cursor && cursor.moveToNext()) {
            val pandora = Clay(
                    name = "全部图片",
                    data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)),
                    id = 0,
                    check = true
            )
            arrayList.add(pandora)
            cursor.moveToFirst()
            do {
                pandora.count++
                val name: String = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
                if (hashMap.keys.contains(name)) {
                    (hashMap[name] as Clay).count++
                } else {
                    val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE))
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                    if (File(path).exists() && 10 <= (size shr 10)) {
                        val album = Clay(
                                name = name,
                                id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)),
                                count = 1,
                                data = path
                        )
                        hashMap.put(name, album)
                        arrayList.add(album)
                    }
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
        return arrayList
    }

    /**
     * 根据相册 ID 获取对应相册下的照片
     */
    fun obtainPandoraWithAlbumName(parentId: Long): MutableList<Clay> {
        val arrayList = mutableListOf<Clay>()
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.BUCKET_ID,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.DATE_ADDED,
                        MediaStore.Images.ImageColumns.DATE_MODIFIED,
                        MediaStore.Images.ImageColumns.DATE_TAKEN,
                        MediaStore.Images.ImageColumns.SIZE,
                        MediaStore.Images.ImageColumns.WIDTH,
                        MediaStore.Images.ImageColumns.HEIGHT,
                        MediaStore.Images.ImageColumns.MIME_TYPE,
                        MediaStore.Images.ImageColumns.TITLE,
                        MediaStore.Images.ImageColumns.ORIENTATION
                ),
                "${MediaStore.Images.ImageColumns.BUCKET_ID} = ?",
                arrayOf(parentId.toString()),
                MediaStore.Images.ImageColumns.DATE_ADDED
        )
        if (null != cursor && cursor.moveToNext()) {
            cursor.moveToLast()
            do {
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE))
                if (File(path).exists() && (10 <= (size shr 10))) {
                    arrayList.add(Clay(
                            data = path,
                            id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)),
                            parentId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)),
                            name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)),
                            parentName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)),
                            addDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED)),
                            modifyDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED)),
                            takenDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN)),
                            title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE)),
                            mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE)),
                            size = size,
                            width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH)),
                            height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT)),
                            orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION))
                    ))
                }
            } while (cursor.moveToPrevious())
            cursor.close()
        }
        return arrayList
    }
}