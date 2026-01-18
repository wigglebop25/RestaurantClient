import java.util.Properties
import java.net.URI

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val envProperties = Properties().apply {
    val envFile = rootProject.file(".env/.env")
    if (envFile.exists()) {
        envFile.inputStream().use { load(it) }
    }
}

val apiBaseUrl = envProperties.getProperty("API_BASE_URL")
    ?: System.getenv("API_BASE_URL")
    ?: error("API_BASE_URL is not configured. Add it to .env/.env or as an environment variable.")

val sslCertificate = envProperties.getProperty("SSL_CERTIFICATE")
    ?: System.getenv("SSL_CERTIFICATE")
    ?: ""

// Parse domain from URL
val apiDomain = URI(apiBaseUrl.trim().replace("\"", "")).host ?: "localhost"

// Generate Security Files Task
tasks.register("generateSecurityFiles") {
    doLast {
        // 1. Generate server_cert.crt
        if (sslCertificate.isNotEmpty()) {
            val certFile = file("src/main/res/raw/server_cert.crt")
            certFile.parentFile.mkdirs()
            // Replace literal \n with actual newlines if needed, or handle based on how it's stored
            val formattedCert = sslCertificate.replace("\\n", "\n").replace("\"", "")
            certFile.writeText(formattedCert)
            println("Generated server_cert.crt")
        } else {
            println("WARNING: SSL_CERTIFICATE not found in .env. App may fail to connect.")
        }

        // 2. Generate network_security_config.xml
        val trustAnchors = if (sslCertificate.isNotEmpty()) {
            """
            <trust-anchors>
                <certificates src="@raw/server_cert"/>
            </trust-anchors>
            """.trimIndent()
        } else {
            """
            <trust-anchors>
                <certificates src="system"/>
            </trust-anchors>
            """.trimIndent()
        }

        val securityConfigContent = """<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config>
        <domain includeSubdomains="true">$apiDomain</domain>
        $trustAnchors
    </domain-config>
</network-security-config>"""
        
        val configFile = file("src/main/res/xml/network_security_config.xml")
        configFile.parentFile.mkdirs()
        configFile.writeText(securityConfigContent)
        println("Generated network_security_config.xml with domain: $apiDomain")
    }
}

// Hook into build process
tasks.named("preBuild") {
    dependsOn("generateSecurityFiles")
}

val buildConfigBaseUrl = apiBaseUrl.trim().replace("\"", "\\\"")
val debugForceAdminRole = (project.findProperty("forceAdminRole") as? String)?.equals("true", ignoreCase = true) ?: false
val debugForceAdminUsernameRaw = (project.findProperty("forceAdminUsername") as? String)?.trim() ?: ""
val debugForceAdminUsername = debugForceAdminUsernameRaw.replace("\"", "\\\"")

android {
    namespace = "com.restaurantclient"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.restaurantclient"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.restaurantclient.HiltTestRunner"
        buildConfigField("String", "BASE_URL", "\"$buildConfigBaseUrl\"")
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "FORCE_ADMIN_ROLE", debugForceAdminRole.toString())
            buildConfigField("String", "FORCE_ADMIN_USERNAME", "\"$debugForceAdminUsername\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "FORCE_ADMIN_ROLE", "false")
            buildConfigField("String", "FORCE_ADMIN_USERNAME", "\"\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }

    // Always rename APK to RestaurantClient.apk for all build types
    applicationVariants.all {
        outputs.all {
            val outputImpl = this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
            outputImpl?.outputFileName = "RestaurantClient-${name}.apk"
        }
    }
}

dependencies {

    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")
    
    // Custom Canvas implementation used instead of external libraries
    // implementation("androidx.graphics:graphics-charts:1.0.0-alpha05")
    // implementation("com.google.accompanist:accompanist-charts:0.33.2-alpha")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Retrofit & Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // ViewModel & LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Hilt (for dependency injection)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Secure Storage
    implementation(libs.androidx.security.crypto)
    
    // BlurView for glassmorphism
    implementation(libs.blurview)
    
    // Coil for image loading with GIF support
    implementation(libs.coil.core)
    implementation(libs.coil.gif)
    
    // Lottie for smooth animations
    implementation(libs.lottie)
    
    // MPAndroidChart for analytics graphs
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.core.testing)
}
