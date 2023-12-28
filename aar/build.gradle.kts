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

tasks {
    val sourcesJar by registering(Jar::class) {
        group = "build"
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }

    dokkaJavadoc {
        outputDirectory.set(file("$buildDir/javadoc"))
    }

    val androidJavadocsJar by registering(Jar::class) {
        group = "build"
        archiveClassifier.set("javadoc")
        dependsOn("dokkaJavadoc")
        from("$buildDir/javadoc")
    }
}

afterEvaluate {
    publishing {
        // repositories { UtilsKt.outputMavenGitHubPackages(it, project) }
        publications {
            register<MavenPublication>("releaseAar") {
                from(components["release"])
                artifact(tasks["androidJavadocsJar"])
                artifact(tasks["sourcesJar"])
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
}
