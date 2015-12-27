# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidFile\AndroidSDK/tools/proguard/proguard-android.txt
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
-optimizationpasses 10

-keep class javax.mail.**{*;}
-keep class com.sun.mail.**{*;}
-keep class javax.activation.**{*;}
-keep class org.apache.harmony.**{*;}
-keep class java.security.**{*;}

-keep class android.support.v7.widget.SearchView { *; }

-dontwarn javax.mail.**
-dontwarn javax.activation.**
-dontwarn javax.mail.internet.**
-dontwarn java.awt.**
-dontwarn javax.security.**
-dontwarn com.sun.mail.imap.protocol.**