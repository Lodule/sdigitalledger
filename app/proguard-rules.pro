# Add project specific ProGuard rules here.

# Preserve Line Number Table and SourceFile for crash reporting / stack trace de-obfuscation
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room Database keep rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep entities and DAO implementation classes
-keep class com.sdigitalledger.data.entities.** { *; }
-keep class com.sdigitalledger.data.** { *; }

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**
