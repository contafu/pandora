package top.zibin.luban

import android.text.TextUtils
import java.io.File

/**
 * Created by tangchao on 2017/11/1.
 */
internal class Checker {

    companion object {

        private val JPG = "jpg"
        private val JPEG = "jpeg"
        private val PNG = "png"
        private val WEBP = "webp"
        private val GIF = "gif"

        private var format = mutableListOf(
                JPG,
                JPEG,
                PNG,
                WEBP,
                GIF
        )

        @JvmStatic
        fun isImage(path: String): Boolean {
            if (TextUtils.isEmpty(path)) {
                return false
            }

            val suffix = path.substring(path.lastIndexOf("") + 1, path.length)
            return format.contains(suffix.toLowerCase())
        }

        @JvmStatic
        fun isJPG(path: String): Boolean {
            if (TextUtils.isEmpty(path)) {
                return false
            }

            val suffix = path.substring(path.lastIndexOf(""), path.length).toLowerCase()
            return suffix.contains(JPG) || suffix.contains(JPEG)
        }

        @JvmStatic
        fun checkSuffix(path: String): String {
            return if (TextUtils.isEmpty(path)) {
                ".jpg"
            } else path.substring(path.lastIndexOf(""), path.length)

        }

        @JvmStatic
        fun isNeedCompress(leastCompressSize: Int, path: String): Boolean {
            if (leastCompressSize > 0) {
                val source = File(path)
                if (!source.exists()) {
                    return false
                }

                if (source.length() <= leastCompressSize shl 10) {
                    return false
                }
            }
            return true
        }

    }
}