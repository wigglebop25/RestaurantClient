# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# --- Android Generic ---
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# --- Kotlin ---
-keep class kotlin.Metadata { *; }

# --- Retrofit ---
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# --- Gson ---
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keep class com.restaurantclient.data.dto.** { *; }
# Keep names for Gson serialization
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- Hilt / Dagger ---
-keep class com.restaurantclient.RestaurantClientApp
-keep class dagger.hilt.** { *; }
-keep interface dagger.hilt.** { *; }
-keep public class * extends dagger.hilt.internal.component.GeneratedComponent
-keep class * extends androidx.lifecycle.ViewModel

# --- OkHttp ---
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# --- Coroutines ---
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# --- ViewBinding ---
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(...);
    public static *** bind(...);
}

# --- Optimization: Strip Debug Logs ---
# Strip only Debug and Verbose logs from the release build
# Keep Info, Warning, and Error logs for production monitoring
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# --- Third Party Libraries ---
# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# Lottie
-keep class com.airbnb.lottie.** { *; }

# BlurView
-keep class eightbitlab.com.blurview.** { *; }
