plugins {
    `version-catalog`
    `maven-publish`
    alias( libs.plugins.caupain)
    alias(libs.plugins.semver0)
}
//semver {
//    noAutoBump = properties.getOrDefault("semver.noAutoBump", "false").toString().toBoolean()
//}
semver {
    gitDir.set( rootDir.parentFile.resolve(".git"))
}
group = "es.ibermutua.telefonia"
//version = project.ext.properties.getOrDefault("tag", "0.0.1") as String
version = project.properties.getOrDefault("tag", "0.0.1") as String

catalog {
    versionCatalog {
        from(files("gradle/libs.versions.toml"))
    }
}

// This configures the maven-publish plugin
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version.toString()
            from(components["versionCatalog"])
        }
    }

    val urlUploadRepositoryReleases: String by project
    val artifactoryUsername: String by project
    val artifactoryPassword: String by project

    repositories {
        maven {
            credentials {
                username = artifactoryUsername
                password = artifactoryPassword
            }
            url = uri(urlUploadRepositoryReleases)
            isAllowInsecureProtocol = true
        }
    }
}
