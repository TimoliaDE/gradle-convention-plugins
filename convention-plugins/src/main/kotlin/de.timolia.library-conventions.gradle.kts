// Define Java Library conventions for this organization.
// Projects need to use the organization's Java conventions and publish using Maven Publish

plugins {
    `java-library`
    `maven-publish`
    id("de.timolia.java-conventions")
}

// Projects have the 'com.myorg' group by convention
group = "de.timolia"

val isLive = System.getenv("IS_PIPELINE_LIVE") != null
//group = System.getenv("CI_PROJECT_NAMESPACE")?.replace("/", ".") ?: "local"
version = "git-" + (System.getenv("CI_COMMIT_REF_NAME") ?: "local")



publishing {
    publications {
        register("release", MavenPublication::class) {
            pom {
                properties.put("CI_COMMIT_REF_NAME", System.getenv("CI_COMMIT_REF_NAME") ?: "local")
                properties.put("CI_PIPELINE_ID", System.getenv("CI_PIPELINE_ID") ?: "local")
                properties.put("CI_PROJECT_NAME", System.getenv("CI_PROJECT_NAME") ?: "local")
            }
            from(components["java"])
        }

        // publish if live pipeline in dev repository
        if (isLive) {
            repositories {
                maven {
                    url = uri("${System.getenv("MAVEN_DEV_URL") as String}/${System.getenv("MAVEN_DEV_REPO") as String}")
                    name = "Reposilite-Dev"
                    authentication {
                        create<BasicAuthentication>("basic")
                    }
                    credentials {
                        username = System.getenv("MAVEN_DEV_USER") as String
                        password = System.getenv("MAVEN_DEV_TOKEN") as String
                    }
                }
            }
        }

        // choose credentials based on environment dev/live
        repositories {
            maven {
                url = uri("${project.extra["mavenUrl"] as String}/${project.extra["repository"] as String}")
                name = "Reposilite"
                authentication {
                    create<BasicAuthentication>("basic")
                }
                credentials {
                    username = project.extra["mavenUser"] as String
                    password = project.extra["mavenToken"] as String
                }
            }
        }
    }
}

// The project requires libraries to have a README containing sections configured below
val readmeCheck by tasks.registering(de.timolia.ReadmeVerificationTask::class) {
    readme.set(layout.projectDirectory.file("README.md"))
    readmePatterns.set(listOf("^## API$", "^## Changelog$"))
}

tasks.named("check") { dependsOn(readmeCheck) }
