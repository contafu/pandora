# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/tangchao/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-dontskipnonpubliclibraryclassmembers
#-printconfiguration
#-keep,allowobfuscation @interface android.support.annotation.Keep
#
#-keep @android.support.annotation.Keep class *
#-keepclassmembers class * {
#    @android.support.annotation.Keep *;
#}
#---------------------------------------- 配置区 ----------------------------------------#
-optimizationpasses 5
#-dontshrink #关闭压缩
-dontoptimize #关闭优化
#-dontobfuscate #关闭混淆
-dontpreverify #关闭校验
-useuniqueclassmembernames
-allowaccessmodification
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

#---------------------------------------- 默认保留区 ----------------------------------------#
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectOutputStream);
    java.lang.Object writeReplace();
    java.lang.Object ReadResolve();
}
-keep class **.R$* {
    *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

#---------------------------------------- kotlin 区 ----------------------------------------#
-dontwarn kotlin.**
-dontwarn kotlinx.**

#---------------------------------------- databinding 区 ----------------------------------------#
-keep class android.databinding.** {*;}
-dontwarn android.databinding.**

#---------------------------------------- webview 区 ----------------------------------------#
-keepclassmembers class fqcn.of.javascript.interfact.for.Webview {
    public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WenView, java.lang.String);
}

#---------------------------------------- hybrid 区 ----------------------------------------#
#无

#---------------------------------------- 反射区 ----------------------------------------#
#无

#---------------------------------------- 实体类区 ----------------------------------------#
#见业务区

#---------------------------------------- 第三方区 ----------------------------------------#
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

-dontwarn cn.com.bsfit.**
-keep class cn.com.bsfit.** {*;}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

-dontwarn com.squareup.okhttp.**

#---------------------------------------- 业务区 ----------------------------------------#
