pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        google()
        mavenCentral()

        // 🔥 THIS IS THE REAL SOURCE
        maven {
            url = uri("https://maven.pkg.github.com/maplibre/maplibre-native")
            credentials {
                username = "maplibre"
                password = "maplibre"
            }
        }
    }
}

rootProject.name = "ReconMaps"
include(":app")