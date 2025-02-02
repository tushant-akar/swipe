# General Rules
# Keep all public classes and their public/protected members.
-keep public class * {
    public protected *;
}

# Preserve line numbers and source file names for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Preserve annotations (important for libraries like Retrofit, Room, etc.).
-keepattributes *Annotation*

# Preserve generic signatures (important for libraries using reflection).
-keepattributes Signature

# Preserve exceptions and throwable classes.
-keep class * extends java.lang.Exception

# Remove logging calls (optional, uncomment if you want to remove logs in release builds).
# -assumenosideeffects class android.util.Log {
#     public static *** d(...);
#     public static *** v(...);
#     public static *** i(...);
#     public static *** w(...);
#     public static *** e(...);
# }

# Kotlin-specific rules
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }

# Keep Kotlin coroutines-related classes.
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Room Database
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Keep entity classes annotated with @Entity.
-keep @androidx.room.Entity class * { *; }

# Keep DAO interfaces annotated with @Dao.
-keep @androidx.room.Dao interface * { *; }

# Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# Keep Retrofit interfaces and their methods.
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Keep model classes used with Gson.
-keep class com.tushant.swipe.data.model.** { *; }

# OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Glide (if used for image loading)
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Koin Dependency Injection
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# WebView (if applicable)
# Uncomment this if your app uses WebView with JavaScript.
# -keepclassmembers class fqcn.of.javascript.interface.for.webview {
#     public *;
# }

# WorkManager
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# Custom rules for your app
# Ensure that SyncWorker and related classes are not obfuscated.
-keep class com.tushant.swipe.data.db.SyncWorker { *; }

# Keep ProductRepository and its methods.
-keep class com.tushant.swipe.data.repository.ProductRepository { *; }

# Keep ProductViewModel and its methods.
-keep class com.tushant.swipe.viewModel.ProductViewModel { *; }

# Keep utility classes.
-keep class com.tushant.swipe.utils.** { *; }

# Keep adapters and UI-related classes.
-keep class com.tushant.swipe.view.adapter.** { *; }
-keep class com.tushant.swipe.view.ui.** { *; }