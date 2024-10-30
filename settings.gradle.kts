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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
<<<<<<< HEAD
        maven("https://jitpack.io")

    }

}

rootProject.name = "AlphaKid_v8"
include(":app")
include(":opencv")
project(":opencv").projectDir = file("opencv/sdk")
=======
    }
}

rootProject.name = "AlphaKid"
include(":app")
 
>>>>>>> e1cc0e856e0aa64f442571383c1bd37f4226fe7c
