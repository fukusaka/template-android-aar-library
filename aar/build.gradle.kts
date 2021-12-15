plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.dokka") version "1.6.0"
    id("maven-publish")
}

group = "org.example"
version = "1.0-SNAPSHOT"

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 24
        targetSdk = 31

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
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")

    // Material Design
    implementation("com.google.android.material:material:1.4.0")

    // Unit Test
    testImplementation("junit:junit:4.13.2")

    // Android Test
    androidTestUtil("androidx.test:orchestrator:1.4.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
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
