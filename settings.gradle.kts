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

        maven {
            name = "byDataGitLabPackages"
            url = uri("https://gitlab.com/api/v4/projects/81760046/packages/maven")
            credentials(HttpHeaderCredentials::class) {
                name = "Private-Token"
                value = providers.gradleProperty("byDataGitLabToken").get()
            }
            authentication { create<HttpHeaderAuthentication>("header") }
            content { includeGroup("com.bydata") }   // keep other deps off this repo
        }
    }
}

rootProject.name = "byDataSdk"
include(":app")
