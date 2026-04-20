# Kotlin
-keepattributes *Annotation*
-keepclassmembers class kotlin.Metadata { *; }

# Decompose
-keep class com.arkivanov.decompose.** { *; }

# Kotlinx Serialization
-keepattributes InnerClasses
-keep class kotlinx.serialization.** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Lyricist
-keep class de.nogaemer.unspeakable.i18n.** { *; }

# Lucide icons — prevent R8 from removing icon objects accessed by reflection/string
-keep class com.composables.icons.lucide.** { *; }