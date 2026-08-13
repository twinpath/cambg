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

# Preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Rename source file attributes to keep stack traces clean but obfuscated
-renamesourcefileattribute SourceFile

# --- Room Database Rules ---
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.RoomDatabase$Callback
-dontwarn androidx.room.paging.**

# --- Moshi Rules ---
# Keep Moshi's generated JsonAdapters and annotated classes
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}
# Keep classes annotated with JsonClass to ensure reflection/codegen works
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class *$$JsonAdapter { *; }

# --- Retrofit & OkHttp Rules ---
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @retrofit2.http.** <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# --- Firebase Rules ---
-keepattributes *Annotation*,Signature
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }

# --- Keep Model / Data Classes ---
# Menjaga semua data classes di dalam paket UI dan Data agar serialization/deserialization Room & Retrofit tidak pecah
-keep class com.twinpath.cambg.data.** { *; }
-keep class com.twinpath.cambg.model.** { *; }
