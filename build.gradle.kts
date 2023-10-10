plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "de.timolia.conventions"
version = "1.5"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/TimoliaDE/gradle-convention-plugins")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}

