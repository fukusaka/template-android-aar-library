plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

group = "org.example"
version = "1.0-SNAPSHOT"

android {
    namespace = "com.example.android.aar"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        aarMetadata {
            minCompileSdk = 29 // AGP4.1 から利用側の最小 compileSdk を指定できる
            @Suppress("UnstableApiUsage")
            minAgpVersion = "7.1.0" // AGP7.1 から利用側の最小 AGP を指定できる
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments += mapOf(
            "clearPackageData" to "true"
        )
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
}

dependencies {
    // AndroidX
    implementation(libs.core.ktx)
    implementation(libs.appcompat)

    // Material Design
    implementation(libs.android.material)

    // Unit Test
    implementation(libs.junit)
    
    // Android Test
    androidTestUtil(libs.androidx.test.orchestrator)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}

android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
        multipleVariants {
            allVariants()
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("Aar") {
            // afterEvaluate { from(components["release"]) } // release のみ公開する
            afterEvaluate { from(components["default"]) }
            artifactId = "hello-aar"
        }
    }

    repositories {
        val uploadMavenUser = project.findPropertyAsString("uploadMavenUser")
        val uploadMavenPassword = project.findPropertyAsString("uploadMavenPassword")

        if (uploadMavenUser != null && uploadMavenPassword != null) {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/fukusaka/template-android-aar-library")
                credentials {
                    username = uploadMavenUser
                    password = uploadMavenPassword
                }
            }
        }
    }
}
