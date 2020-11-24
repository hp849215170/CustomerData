# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

#---------------------------------1.实体类---------------------------------
-keep class m.hp.customerdata.entity.*{*;}
#-------------------------------------------------------------------------
#---------------------------------2.第三方包-------------------------------
#okhttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.*{*;}
-keepattributes Signature
-keepattributes *Annotation*
# FastJson
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.* {*;}
-keepattributes Signature
-keepattributes *Annotation*
# apache.poi
-keep class com.microsoft.schemas.office.x2006.** {*;}
-keep class schemaorg_apache_xmlbeans.**{*;}
-keep class schemasMicrosoftComOfficeExcel.**{*;}
-keep class schemasMicrosoftComOfficeOffice.**{*;}
-keep class schemasMicrosoftComVml.**{*;}
-keep class aavax.xml.**{*;}
-keep class com.bea.xml.stream.**{*;}
-keep class com.wutka.dtd.**{*;}
-keep class org.apache.**{*;}
-keep class org.w3c.**{*;}
-keep class repackage.**{*;}
-keep class schemaorg_apache_xmlbeans.**{*;}
-keep class org.etsi.uri.x01903.v13.** {*;}
-keep class org.openxmlformats.schemas.** {*;}
-keep class org.w3.x2000.x09.xmldsig.** {*;}

#-------------------------------------------------------------------------
#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
-dontskipnonpubliclibraryclassmembers
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#-ignorewarnings
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
#-keep public class com.android.vending.licensing.ILicensingService
#-keep class android.support.** {*;}
-keep public class * extends android.os.IInterface

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
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

-keepclasseswithmembernames class * { # 保持 native 方法不被混淆
 native <methods>;
}

-keepclasseswithmembers class * { # 保持自定义控件类不被混淆
 public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
 public void *(android.view.View);
}

-keepclassmembers enum * { # 保持枚举 enum 类不被混淆
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
 public static final android.os.Parcelable$Creator *;
}

#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-keepclassmembers class * extends android.webkit.webViewClient {
#    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
#    public boolean *(android.webkit.WebView, java.lang.String);
#}
#-keepclassmembers class * extends android.webkit.webViewClient {
#    public void *(android.webkit.webView, jav.lang.String);
#}
#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------


-dontwarn javax.xml.**
-dontwarn org.w3c.dom.events.EventListener
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.apache.poi.wp.usermodel.CharacterRun
-dontwarn org.springframework.**
-dontwarn retrofit2.**
-dontwarn java.awt.Graphics
-dontwarn javax.ws.**
-dontwarn javax.xml.crypto.dsig.TransformService
-dontwarn org.glassfish.jersey.internal.spi.AutoDiscoverable
-dontwarn com.sun.javadoc.Doclet
-dontwarn org.springframework.**
-dontwarn java.awt.**
-dontwarn org.apache.poi.wp.usermodel.Paragraph
-dontwarn org.apache.tools.ant.taskdefs.MatchingTask