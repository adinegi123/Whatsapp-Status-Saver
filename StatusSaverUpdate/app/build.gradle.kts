plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
}

android {
    namespace = "com.adi121.statussaverupdate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adi121.statussaverupdate"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.app.update.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.google.android.material:material:1.6.1")

    // Navigation Components dependency
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    //Swipe to refresh dependency
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Sdp dependency
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    implementation("uk.co.samuelwall:material-tap-target-prompt:3.3.2")

    // Document file compat dependency
    implementation("com.lazygeniouz:dfc:0.8")

    // circular-imageview dependency
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Glide dependency
    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    //EASY Permission dependency
    implementation("pub.devrel:easypermissions:3.0.0")

    //Apache dependency
    implementation("org.apache.commons:commons-lang3:3.5")
    implementation("commons-io:commons-io:2.4")

    implementation ("com.google.android.exoplayer:exoplayer-core:2.16.1")
    implementation ("com.google.android.exoplayer:exoplayer-dash:2.16.1")
    implementation ("com.google.android.exoplayer:exoplayer-ui:2.16.1")
}