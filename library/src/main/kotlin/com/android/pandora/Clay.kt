package com.android.pandora

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by tangchao on 2017/8/14.
 */
internal data class Clay(
        var data: String? = "", /*文件路径*/
        var id: Long = 0, /*文件 id*/
        var parentId: Long = 0, /*文件所在组 id*/
        var name: String? = "", /*文件名*/
        var parentName: String? = "", /*文件所在组名*/
        var addDate: Long = 0, /*文件加入时间戳*/
        var modifyDate: Long = 0, /*文件修改时间戳*/
        var takenDate: Long = 0, /*图片文件生成时间戳*/
        var title: String? = "", /*文件标题*/
        var mimeType: String? = "", /*文件类型*/
        var size: Long = 0, /*文件大小*/
        var width: Int = 0, /*图片文件宽度*/
        var height: Int = 0, /*图片文件高度*/
        var orientation: Int = 0, /*图片文件方向*/
        var count: Int = 0, /*作为Album时文件个数*/
        var check: Boolean = false/*文件是否被选中*/
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readLong(),
            source.readLong(),
            source.readString(),
            source.readString(),
            source.readLong(),
            source.readLong(),
            source.readLong(),
            source.readString(),
            source.readString(),
            source.readLong(),
            source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readInt(),
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(data)
        writeLong(id)
        writeLong(parentId)
        writeString(name)
        writeString(parentName)
        writeLong(addDate)
        writeLong(modifyDate)
        writeLong(takenDate)
        writeString(title)
        writeString(mimeType)
        writeLong(size)
        writeInt(width)
        writeInt(height)
        writeInt(orientation)
        writeInt(count)
        writeInt((if (check) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Clay> = object : Parcelable.Creator<Clay> {
            override fun createFromParcel(source: Parcel): Clay = Clay(source)
            override fun newArray(size: Int): Array<Clay?> = arrayOfNulls(size)
        }
    }
}