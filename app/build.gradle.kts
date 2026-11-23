plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.tatanstudios.astropollococina"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.tatanstudios.astropollococina"
        minSdk = 27
        targetSdk = 36
        versionCode = 7
        versionName = "3.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // debug y release apuntan al servidor de produccion.
        // Para probar contra Laragon local, usa en debug: http://192.168.1.4:8080/api/ y http://192.168.1.4:8080/storage/imagenes/
        debug {
            buildConfigField("String", "BASE_URL", "\"https://astropollo.tatancloud.com/api/\"")
            buildConfigField("String", "URL_IMAGENES", "\"https://astropollo.tatancloud.com/storage/imagenes/\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://astropollo.tatancloud.com/api/\"")
            buildConfigField("String", "URL_IMAGENES", "\"https://astropollo.tatancloud.com/storage/imagenes/\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {

    // Compose (todas las versiones salen del BOM, sin mezclar versiones sueltas)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material")              // PullRefresh
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.runtime:runtime-livedata")

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Red
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.5")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")

    // Notificaciones
    implementation("com.onesignal:OneSignal:5.11.0")   // version fija: builds reproducibles

    // UI: imágenes, animaciones y avisos
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.airbnb.android:lottie-compose:6.3.0")
    implementation("com.github.GrenderG:Toasty:1.5.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}