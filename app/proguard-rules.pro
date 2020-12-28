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



# 指定代码的压缩级别
-optimizationpasses 5
# 混淆后类名都为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses
# 不做预校验的操作
-dontpreverify
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 不混淆项目的注释、资源文件、资源目录名、内部类、泛型、抛出异常时保留代码行号
-keepattributes *Annotation*,SourceFile,SourceDir,InnerClasses,Signature,LineNumberTable

###-----------基本配置-不能被混淆的------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

#support.v4/v7包不混淆
-keep class android.support.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v7.**
-keep interface android.support.v7.app.** { *; }
-dontwarn android.support.**    # 忽略警告
-keep interface android.support.design.** {*; }
-keep interface android.support.design.** {*; }
-dontwarn android.support.design.**

#手动启用support keep注解
-printconfiguration
-keep,allowobfuscation @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclassmembers class * {
    @android.support.annotation.Keep *;
}

#保持注解继承类不混淆
-keep class * extends java.lang.annotation.Annotation {*;}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  static ** CREATOR;
  <fields>;
  <methods>;
}

#保持Serializable实现类不被混淆
-keepnames class * implements java.io.Serializable
#保持Serializable不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
  static final long serialVersionUID;
  private static final java.io.ObjectStreamField[] serialPersistentFields;
  !static !transient <fields>;
  !private <fields>;
  !private <methods>;
  private void writeObject(java.io.ObjectOutputStream);
  private void readObject(java.io.ObjectInputStream);
  java.lang.Object writeReplace();
  java.lang.Object readResolve();
}
#保持枚举enum类不被混淆
-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#自定义组件不被混淆
-keep public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

#不混淆资源类
-keepclassmembers class **.R$* {
  public static <fields>;
}
#保持 native 方法不被混淆
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-keepclassmembers class * {
    void *(**On*Event);
}
#---------------------------------webview------------------------------------

-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
#----------------------------------------------------------------------------

#####################记录生成的日志数据,gradle build时在本项目根目录输出################
#混淆时记录日志
-verbose
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#生成原类名和混淆后的类名的映射文件
-printmapping proguardMapping.txt

-keepattributes *Annotation*




-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
#    public static int e(...);
}


-keepclasseswithmembers,includedescriptorclasses class kotlin.**{*;}

-keepclasseswithmembers,includedescriptorclasses class android.arch.**{*;}




-dontwarn kotlin.internal.PlatformImplementationsKt
-dontnote kotlin.internal.PlatformImplementationsKt

-dontnote kotlin.jvm.internal.Reflection
-dontwarn kotlin.jvm.internal.Reflection


-dontnote kotlin.coroutines.jvm.internal.DebugMetadataKt


-dontwarn android.webkit.WebView



#androidx 混淆配置
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**



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
#eventbus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.*{*;}
-keep class android.support.*{*;}

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

#---------------------------------1.实体类---------------------------------
-keep class m.hp.customerdata.entity.*{*;}
#-------------------------------------------------------------------------