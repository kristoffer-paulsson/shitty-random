plugins {
    id("java")
    id("application")
    id("distribution")
}

group = "org.example"
version = "1.0-SNAPSHOT"

val mainClass = "org.example.Main"

repositories {
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
    mainClass.set(mainClass)
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

tasks.register<Exec>("createMacInstaller") {
    group = "distribution"
    description = "Create a macOS installer using jpackage"
    commandLine = listOf(
        "jpackage",
        "--type", "pkg",
        "--input", "build/install/shitty-random",
        "--dest", "build/installer",
        "--name", "ShittyRandom",
        "--main-class", mainClass,
        "--main-jar", "lib/shitty-random-" + version + ".jar", //"shitty-random.jar",
        "--icon", "src/main/resources/logo.icns"
    )
}

tasks.register<Exec>("createDebInstaller") {
    group = "distribution"
    description = "Create a Debian installer using jpackage"
    commandLine = listOf(
        "jpackage",
        "--type", "deb", // or "rpm" for RPM package
        "--input", "build/install/shitty-random",
        "--dest", "build/installer",
        "--name", "ShittyRandom",
        "--main-class", mainClass,
        "--main-jar", "lib/shitty-random-" + version + ".jar",
        "--icon", "src/main/resources/logo.png"
    )
}

tasks.register<Exec>("createRpmInstaller") {
    group = "distribution"
    description = "Create a RedHat installer using jpackage"
    commandLine = listOf(
        "jpackage",
        "--type", "rpm", // or "rpm" for RPM package
        "--input", "build/install/shitty-random",
        "--dest", "build/installer",
        "--name", "ShittyRandom",
        "--main-class", mainClass,
        "--main-jar", "lib/shitty-random-" + version + ".jar",
        "--icon", "src/main/resources/logo.png"
    )
}