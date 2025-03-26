plugins {
    id("java")
    id("application")
    id("distribution")
}

group = "org.example"
version = "1.0-SNAPSHOT"

val mainClazz = "org.example.Main"

repositories {
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
            from(configurations.runtimeClasspath) {
                into("lib")
            }
        }
    }
}

tasks.register<Copy>("setupLocalRepo") {
    group = "build"
    description = "Setup local repository with all dependencies"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDir = file("${projectDir}/local-repo")
    from(configurations.runtimeClasspath) {
        into(relativePath(destinationDir.parent))
    }
    from(configurations.testRuntimeClasspath) {
        into(relativePath(destinationDir.parent))
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
    from(configurations.runtimeClasspath) {
        into("lib")
    }
    archiveFileName.set("shitty-random-${version}.zip")
    destinationDirectory.set(file("$buildDir/distributions"))
}