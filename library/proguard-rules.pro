-dontwarn kotlin.**
-dontwarn kotlinx.**

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

-dontwarn com.squareup.okhttp.**