pluginManagement {
    repositories {
        flatDir {
            dirs("file://${rootDir}/local-repo")
        }
        maven {
            url = uri("file://${rootDir}/local-repo")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "shitty-random"

