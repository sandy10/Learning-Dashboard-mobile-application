# ----------------------------------------------------
# Learning Dashboard Mobile Application - Security & R8 Rules
# ----------------------------------------------------

# Code Optimization & Shrinking
-optimizationpasses 5
-allowaccessmodification

# Strip debug and verbose logging statements in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Preserve kotlinx.serialization runtime models and serializers
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
    @kotlinx.serialization.Serializable <fields>;
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}
-keep class * implements kotlinx.serialization.KSerializer {
    <init>(...);
}

# Preserve Room database entities and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Keep security Keystore parameters and CryptoManager
-keep class com.learning.dashboardmobileapp.core.data.security.** { *; }

# Prevent revealing source file names in stacktraces
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable