import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

val appVersionName: String by rootProject.extra
val appVersionCode: Int    by rootProject.extra

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

//    js {
//        browser()
//        binaries.executable()
//    }
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        binaries.executable()
//    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.cio)
            implementation(libs.androidx.core.ktx.v1160)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.server.core)

            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.server.websockets)

            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)

            implementation(libs.ktor.server.cio)

            implementation(libs.material.kolor)

            implementation(libs.icons.lucide.cmp)

            implementation(libs.decompose)
            implementation(libs.lifecycle.coroutines)
            implementation(libs.extensions1.compose)
            implementation(libs.extensions1.compose.experimental)

            implementation(libs.lyricist)
            implementation(libs.multiplatform.settings)

            implementation(libs.kermit)

            implementation("network.chaintech:qr-kit:3.1.3") {
                exclude(group = "org.bytedeco", module = "opencv-platform")
                exclude(group = "org.bytedeco", module = "leptonica-platform")
                exclude(group = "org.bytedeco", module = "tesseract-platform")
                exclude(group = "org.bytedeco", module = "javacpp-platform")
            }

            implementation(libs.colormath)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.client.okhttp)

            val osName = System.getProperty("os.name").lowercase()
            val arch = System.getProperty("os.arch").lowercase()
            val classifier = when {
                osName.contains("win")                      -> "windows-x86_64"
                osName.contains("mac") && arch == "aarch64" -> "macosx-arm64"
                osName.contains("mac")                      -> "macosx-x86_64"
                osName.contains("nux")                      -> "linux-x86_64"
                else -> null
            }
            if (classifier != null) {
                runtimeOnly("org.bytedeco:opencv:4.6.0-1.5.8:$classifier")
                runtimeOnly("org.bytedeco:javacpp:1.5.8:$classifier")
            }


            configurations {
                all {
                    exclude("org.bytedeco", "artoolkitplus")
                    exclude("org.bytedeco", "ffmpeg")
                    exclude("org.bytedeco", "flandmark")
                    exclude("org.bytedeco", "flycapture")
                    exclude("org.bytedeco", "leptonica")
                    exclude("org.bytedeco", "libdc1394")
                    exclude("org.bytedeco", "libfreenect")
                    exclude("org.bytedeco", "librealsense")
                    exclude("org.bytedeco", "librealsense2")
                    exclude("org.bytedeco", "openblas")
                    exclude("org.bytedeco", "tesseract")
                    exclude("org.bytedeco", "videoinput")
                    // keep only opencv and javacpp if needed
                    // keep only windows-x86_64 classifier
                }
            }
        }
        webMain.dependencies {
            implementation(npm("@js-joda/timezone", "2.22.0"))
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "de.nogaemer.unspeakable"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "de.nogaemer.unspeakable"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionName = appVersionName
        versionCode = appVersionCode

        buildConfigField("String", "VERSION_NAME", "\"$appVersionName\"")
        buildConfigField("int",    "VERSION_CODE", "$appVersionCode")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isDebuggable = true
            ndk {
                abiFilters += "arm64-v8a"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspCommonMainMetadata", libs.lyricist.processor)
}

tasks.configureEach {
    if (name != "kspCommonMainKotlinMetadata" &&
        (name.startsWith("ksp") || name.startsWith("compileKotlin"))
    ) {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

ksp {
    arg("lyricist.generateStringsProperty", "true")
}


compose.desktop {
    application {
        mainClass = "de.nogaemer.unspeakable.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Unspeakable"
            packageVersion = appVersionName
            description = "Unspeakable Party Game"
            copyright = "© 2026 nogaemer"
            vendor = "nogaemer"


            windows {
                dirChooser = true
                menuGroup = "Unspeakable"
                menu = true
                shortcut = true
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
            }
            macOS {
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
            }
            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
            }

            modules(
                "java.base",
                "java.desktop",
                "java.logging",
                "java.management",
                "java.naming",
                "java.net.http",
                "java.sql",
                "java.xml",
                "jdk.unsupported"
            )
        }
    }
}
