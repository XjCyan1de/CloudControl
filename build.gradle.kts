plugins {
    kotlin("jvm") version "1.3.70"
    maven
}

allprojects {
    group = "com.github.xjcyan1de"
    version = "1.0-SNAPSHOT"

    apply(plugin = "kotlin")
    apply(plugin = "maven")

    repositories {
        jcenter()
        maven { setUrl("https://jitpack.io/") }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.3")
        implementation("com.github.CoffeeInjected", "KExtensions", "0.1")
        implementation("com.github.xjcyan1de.cyanlibz", "CyanLibZ-Localization", "1.6")
        implementation("com.github.xjcyan1de.cyanlibz", "CyanLibZ-Messenger", "1.6")
        implementation("com.github.xjcyan1de.cyanlibz", "CyanLibZ-Terminable", "1.6")
        implementation("com.typesafe", "config", "1.4.0")
        implementation("io.github.config4k", "config4k", "0.4.2")
        implementation("com.google.code.gson", "gson", "2.8.6")
        implementation("org.fusesource.jansi", "jansi", "1.18")
        implementation("jline", "jline", "2.14.2")
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
        compileJava {
            options.encoding = "UTF-8"
        }
        jar {
            doFirst {
                from({
                    configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
                })
            }
            manifest {
                attributes(
                    "Main-Class" to "Main",
                    "Implementation-Version" to archiveVersion.get()
                )
            }
        }
    }
}

dependencies {
    implementation(project(":cloudcontrol-api"))
}