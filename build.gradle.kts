plugins {
    id("java")
    id("application")
    id("distribution")
}

group = "org.example"
version = "1.0-SNAPSHOT"

val mainClazz = "org.example.Main"

repositories {
    flatDir {
        dirs("file://${projectDir}/local-repo")
    }
    maven {
        url = uri("file://${projectDir}/local-repo")
    }
    mavenCentral()
}

dependencies {
    implementation("args4j:args4j:2.33")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set(mainClazz)
    applicationName = "shitty-random"
}

distributions {
    main {
        contents {
            from("src/main/resources") {
                into("resources")
            }
        }
    }
}

tasks.register<Copy>("setupLocalRepo") {
    group = "build"
    description = "Setup local repository with all dependencies"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDir = file("${projectDir}/local-repo")
    val relativePath = relativePath(destinationDir.parent)

    // Copy runtime and test runtime classpath
    from(configurations.runtimeClasspath) {
        into(relativePath)
    }
    from(configurations.testRuntimeClasspath) {
        into(relativePath)
    }

    // Copy Gradle plugins
    val gradleUserHome = gradle.gradleUserHomeDir
    val gradlePluginsDir = file("$gradleUserHome/caches/modules-2/files-2.1")
    from(gradlePluginsDir) {
        include("**/*")
    }
    into(destinationDir)

    // Copy Kotlin/Native (konan) dependencies
    val konanUserHome = file("${System.getProperty("user.home")}/.konan")
    val konanDir = file("$konanUserHome/cache")
    from(konanDir) {
        include("**/*")
    }
    into(destinationDir)

    // Counterhack fixing attrocities of IllumiNATO
    doLast {
        val rootDir = file("${projectDir}/local-repo")
        fun traverse(file: File, level: Int) {
            if (file.isDirectory) {
                println("Directory: ${file.path}")
                file.listFiles()?.forEach {
                    if(it.isFile && it.exists() && level > 0){
                        var path = it.path.split('/').toMutableList()
                        path.removeAt(path.lastIndex-1)
                        val newDir = file("/" + path.joinToString("/") + "/")
                        println("TEST: $newDir")
                        //from(it)
                        //into(newDir)
                        if(!newDir.exists()) {
                            it.copyTo(newDir)
                            it.delete()
                        }
                    }
                    traverse(it, level + 1)
                }
            } else {
                println("File: ${file.path}")
            }
        }
        traverse(rootDir, 0)
    }

}

tasks.register<Exec>("createMacInstaller") {
    group = "distribution"
    description = "Create a macOS installer using jpackage"
    commandLine = listOf(
        "jpackage",
        "--type", "dmg",
        "--input", "build/install/shitty-random",
        "--dest", "build/installer",
        "--name", "ShittyRandom",
        "--main-class", mainClazz,
        "--main-jar", "lib/shitty-random-" + version + ".jar", //"shitty-random.jar",
        "--icon", "src/main/resources/logo.icns"
    )
}

tasks.register<Exec>("createDebInstaller") {
    group = "distribution"
    description = "Create a Debian installer using jpackage"
    commandLine = listOf(
        "jpackage",
        "--type", "deb",
        "--input", "build/install/shitty-random",
        "--dest", "build/installer",
        "--name", "ShittyRandom",
        "--main-class", mainClazz,
        "--main-jar", "lib/shitty-random-" + version + ".jar",
        "--icon", "src/main/resources/logo.png"
    )
}

tasks.register<Exec>("createRpmInstaller") {
    group = "distribution"
    description = "Create a RedHat installer using jpackage"
    commandLine = listOf(
        "jpackage",
        "--type", "rpm",
        "--input", "build/install/shitty-random",
        "--dest", "build/installer",
        "--name", "ShittyRandom",
        "--main-class", mainClazz,
        "--main-jar", "lib/shitty-random-" + version + ".jar",
        "--icon", "src/main/resources/logo.png"
    )
}

tasks.register<Zip>("createSourcePackage") {
    group = "distribution"
    description = "Package the project for source distribution"
    dependsOn("setupLocalRepo")
    from(".") {
        include(
            "local-repo/**",
            "src/**", "build.gradle.kts", "settings.gradle.kts",
            "gradlew", "gradlew.bat", "gradle/**",
            "LICENSE", "README.md", ".gitignore")
    }
    archiveFileName.set("shitty-random-${version}.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions").get().asFile)
}