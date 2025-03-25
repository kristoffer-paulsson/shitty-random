plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

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
    mainClass.set("org.example.Main")
    applicationName = "ShittyRandom"
}

tasks.register<JavaExec>("runServer") {
    group = "application"
    description = "Run the server daemon"
    mainClass.set("org.example.RandomServer")
    classpath = sourceSets["main"].runtimeClasspath
}