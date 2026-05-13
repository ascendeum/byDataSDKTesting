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

        maven {
            url = uri("https://gitlab.com/api/v4/projects/81760008/packages/maven")

            credentials {
                username = "PRIVATE-TOKEN"
                password = "glpat-wBXCQvKEdH39CQyctBDLYWM6MQpvOjEKdTptaWEydA8.01.171692n6n"
            }
        }
    }
}

rootProject.name = "byDataSdk"
include(":app")
