# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the default configuration files.

# ProGuard rules for common libraries
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Keep all names of your main classes to ensure reflection works
-keep class * extends android.app.Activity
-keep class * extends android.app.Application
-keep class * extends android.app.Service
