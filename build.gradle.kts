plugins {
    `version-catalog`
    `maven-publish`
    `ivy-publish`
    alias(libs.plugins.caupain)
    alias(libs.plugins.semver0)
}

semver {
    // Inherit defaults; can be overridden via -Psemver.* properties.
}

group = "io.github.bayesiano"

println("***version: ${project.version}")
val tag = (project.properties["tag"] as String?).orEmpty()
if (tag.isNotEmpty()) version = tag
println("***version: ${project.version}")

catalog {
    versionCatalog {
        from(files("gradle/libs.versions.toml"))
    }
}

// GitHub repo coordinates used for the Packages registry URL.
val githubOwner = (project.findProperty("github.owner") as String?) ?: "bayesiano"
val githubRepo = (project.findProperty("github.repo") as String?) ?: "my-gradle-catalog"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version.toString()
            from(components["versionCatalog"])
        }

        create<MavenPublication>("gpr") {
            from(components["versionCatalog"])
        }

        create<IvyPublication>("ivy") {
            organisation = project.group as String
            module = project.name
            revision = project.version.toString()
            descriptor.status = "milestone"
            descriptor.branch = "production"

            from(components["versionCatalog"])
        }
    }

    repositories {
        // Optional internal Maven repository (e.g. Artifactory).
        // Only registered when credentials and URL are supplied via Gradle
        // properties or environment variables, so public clones build cleanly.
        val internalUrl = (project.findProperty("urlUploadRepositoryReleases") as String?)
            ?: System.getenv("INTERNAL_MAVEN_URL")
        val internalUser = (project.findProperty("artifactoryUsername") as String?)
            ?: System.getenv("INTERNAL_MAVEN_USER")
        val internalPass = (project.findProperty("artifactoryPassword") as String?)
            ?: System.getenv("INTERNAL_MAVEN_PASSWORD")

        if (!internalUrl.isNullOrBlank() && !internalUser.isNullOrBlank() && !internalPass.isNullOrBlank()) {
            maven {
                name = "internal"
                url = uri(internalUrl)
                isAllowInsecureProtocol = internalUrl.startsWith("http://")
                credentials {
                    username = internalUser
                    password = internalPass
                }
            }
        }

        // GitHub Packages.
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/$githubOwner/$githubRepo")
            credentials {
                username = (project.findProperty("gpr.user") as String?) ?: System.getenv("GITHUB_ACTOR")
                password = (project.findProperty("gpr.key") as String?) ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}