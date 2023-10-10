plugins {
    `maven-publish`
}

fun configureRepository(
    repository: MavenArtifactRepository,
    repositoryName: String,
    repositoryUser: String,
    repositoryPassword: String
) {
    repository.apply {
        url = uri("https://reposilite.timolia.de/$repositoryName")
        name = "Reposilite-$repositoryName"
        authentication {
            create<BasicAuthentication>("basic")
        }
        credentials {
            username = repositoryUser
            password = repositoryPassword
        }
    }
}
// Check if pipeline is live, if the branch is protected isLive will be true
val isLive = System.getenv("IS_PIPELINE_LIVE") != null
val localEnvFile = File(
    "${System.getProperties().getProperty("user.home")}${File.separator}.gradle",
    "env-timolia.local.gradle.kts"
)

// Check if local env file exists
if (localEnvFile.exists()) {
    // Set project extras to local env file
    apply(from = localEnvFile.path)
} else {
    // Pipeline is live so we use live maven repo
    if (isLive) {
        // live credentials + url
        project.extra.set("mavenToken", System.getenv("MAVEN_LIVE_TOKEN") as String)
        project.extra.set("mavenUser", System.getenv("MAVEN_LIVE_USER") as String)
        project.extra.set("repository", System.getenv("MAVEN_LIVE_REPO") as String)
        project.extra.set("mavenUrl", System.getenv("MAVEN_LIVE_URL") as String)
    } else {
        // dev credentials + url
        project.extra.set("mavenToken", System.getenv("MAVEN_DEV_TOKEN") as String)
        project.extra.set("mavenUser", System.getenv("MAVEN_DEV_USER") as String)
        project.extra.set("repository", System.getenv("MAVEN_DEV_REPO") as String)
        project.extra.set("mavenUrl", System.getenv("MAVEN_DEV_URL") as String)
    }
}
fun createPublishConfiguration() {
    publishing {
        publications {
            register("release", MavenPublication::class) {
                createTimoliaPomProperties()
                from(components["java"])
            }
        }
    }
}
publishing {
    publications {
        repositories {
            maven {
                configureRepository(
                    this,
                    project.extra["repository"] as String,
                    project.extra["mavenUser"] as String,
                    project.extra["mavenToken"] as String
                )
            }
        }
        if (isLive) {
            repositories {
                maven {
                    configureRepository(
                        this,
                        System.getenv("MAVEN_DEV_REPO") as String,
                        System.getenv("MAVEN_DEV_USER") as String,
                        System.getenv("MAVEN_DEV_TOKEN") as String
                    )
                }
            }
        }
    }
}

fun MavenPublication.createTimoliaPomProperties() {
    pom {
        properties.put("CI_COMMIT_REF_NAME", System.getenv("CI_COMMIT_REF_NAME") ?: "local")
        properties.put("CI_PIPELINE_ID", System.getenv("CI_PIPELINE_ID") ?: "local")
        properties.put("CI_PROJECT_NAME", System.getenv("CI_PROJECT_NAME") ?: "local")
    }
}