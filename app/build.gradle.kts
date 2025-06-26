@file:Suppress("UnstableApiUsage")
plugins {
    alias(libs.plugins.googleservices)
    alias(libs.plugins.firebasecrashlytics)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.nhm.distributor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nhm.distributor"
        minSdk = 26
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 6
        versionName = "1.0.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }



    buildTypes {
        release {
            isMinifyEnabled = false
//            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    kapt {
        correctErrorTypes = true
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    lint {
        disable += "MissingTranslation" + "TypographyFractions" + "LabelFor" + "SpeakableTextPresentCheck" + "NewerVersionAvailable"
        abortOnError = false
        checkReleaseBuilds =  false
        warningsAsErrors = false // Treat warnings as errors (optional)
        absolutePaths =  false // Use relative paths in lint reports (optional)
        noLines =  false   // Do not include line numbers in lint reports (optional)
        ignoreWarnings  = true    // Ignore all lint warnings (optional)
        checkAllWarnings =  false // Check all lint warnings, including those off by default (optional)
        showAll =  true // Show all lint warnings, including those that are filtered out (optional)
        explainIssues  = true // Explain lint issues in the report (optional)
        quiet = true
    }
}

//composeCompiler {
//    enableStrongSkippingMode = true
//}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.google.material)
    testImplementation(libs.junit)

    implementation (libs.androidx.appcompat)

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)


    implementation (libs.navigationfragment)
    implementation (libs.navigationui)

    //noinspection GradleCompatible,GradleCompatible
    implementation (libs.databindingktx)
    implementation (libs.databindingruntime)

    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.coroutines.android )

    implementation(libs.sdp)
    implementation(libs.ssp)

    implementation(libs.swiperefreshlayout)
    implementation (libs.picasso)

    //retrofit
    implementation (libs.retrofit2.retrofit)
    implementation (libs.retrofit2.converter.gson )
    implementation (libs.retrofit2.converter.scalars)
    implementation (libs.okhttp3.okhttp)
    implementation (libs.okhttp3.logging.interceptor)

    implementation (libs.gson)

    implementation (libs.glide)
    ksp (libs.glideksp)
    implementation(libs.glideokhttp3) {
        exclude("glide-parent", "glide")
    }
//    exclude("glide-parent", "glide")

    implementation (libs.preference)
    implementation (libs.coil)

    implementation (libs.datastorepreferences)
    implementation (libs.datastorepreferencescore)

    implementation (libs.compressor)
    implementation (libs.lottie)
    implementation (libs.flexbox)

    debugImplementation (libs.chucker)
    releaseImplementation (libs.chucker.no.op)

//    implementation (libs.androidPlayCore)
    implementation (platform(libs.firebase.bom))
    implementation (libs.firebase.auth.ktx)
    implementation (libs.firebase.database.ktx)
    implementation (libs.firebase.messaging.ktx)
    implementation (libs.firebase.analytics.ktx)
    implementation (libs.firebase.crashlytics.ktx)
    implementation (libs.firebase.config.ktx)
    implementation (libs.firebase.dynamic.links.ktx)


    implementation (libs.play.services.auth)
    implementation (libs.play.services.location)
    implementation (libs.play.services.maps)

    implementation (libs.stfalconImageViewer)

    implementation (libs.paging.common)
    implementation (libs.paging.runtime)

//    implementation ("com.google.android.play:review-ktx:2.0.1")
    implementation (libs.jsoup)
    implementation (libs.timelineview)

//    implementation ("com.daimajia.swipelayout:library:1.2.0")

    implementation (libs.room.runtime)
    implementation (libs.room.ktx)
    annotationProcessor (libs.room.compiler)
    ksp (libs.room.compiler.ksp)
    ksp (libs.room.ktx.ksp)

    implementation (libs.work.runtime)
    implementation (libs.ccp)
    implementation (libs.mukeshOtpView)

    implementation (libs.media3.exoplayer)
    implementation (libs.media3.ui)
//    implementation (libs.media3ExoplayerHls)

    implementation ("androidx.media3:media3-exoplayer-hls:1.6.1")

    implementation (libs.photoViews)
    implementation (libs.ratingBar)

    implementation (libs.materialratingbar)
    implementation (libs.razorpay)

    implementation (libs.signature)
    implementation (libs.rackMonthPicker)

}

