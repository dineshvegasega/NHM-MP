pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
//        maven { url = uri("https://plugins.gradle.org/m2/") }
//        maven { url = uri("http://repo1.maven.org/maven2") }
//        maven { url = uri("https://maven.google.com") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
//        maven { url = uri("https://plugins.gradle.org/m2/") }
//        maven { url = uri("https://maven.google.com") }
    }
}

rootProject.name = "NHM"
include(":app")
 