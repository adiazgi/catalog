import com.javiersc.semver.project.gradle.plugin.SemverExtension
import com.javiersc.semver.project.gradle.plugin.tasks.PrintSemverTask
import org.gradle.kotlin.dsl.provideDelegate

plugins {
    `version-catalog`
    `maven-publish`
    `ivy-publish`
    alias( libs.plugins.caupain)
    alias(libs.plugins.semver0)
}
//semver {
//    noAutoBump = properties.getOrDefault("semver.noAutoBump", "false").toString().toBoolean()
//}
semver {
//    gitDir.set( rootDir.parentFile.resolve(".git"))
}
group = "es.ibermutua.telefonia"
//version = project.ext.properties.getOrDefault("tag", "0.0.1") as String
println("***version: ${project.version}")
val tag = project.findProperty("tag") as String?
if( tag != null) version = tag
println("***version: ${project.version}")

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

        create<MavenPublication>("gpr") {
            from(components["versionCatalog"])
        }
//        create<MavenPublication>("my-local-maven") {
//            groupId = project.group as String
//            artifactId = project.name
//            version = project.version.toString()
//
//
//            from(components["versionCatalog"])
//        }

        create<IvyPublication>("ivy") {
            organisation = "es.ibermutua.telefonia"
            module = project.name
            revision = project.version.toString()
            descriptor.status = "milestone"
            descriptor.branch = "production"
//            descriptor.extraInfo( "http://my.namespace", "myElement", "Some value")

            from(components["versionCatalog"])
        }
    }

    val urlUploadRepositoryReleases = project.property("urlUploadRepositoryReleases") as String
    val artifactoryUsername = project.property("artifactoryUsername") as String
    val artifactoryPassword = project.property("artifactoryPassword") as String

    repositories {
        maven {
            credentials {
                username = artifactoryUsername
                password = artifactoryPassword
            }
            url = uri(urlUploadRepositoryReleases)
            isAllowInsecureProtocol = true
        }

        maven( "github") {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/adiazgi/catalog")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }


//        maven("my-local-maven") {
//            url = uri("${File.listRoots()[0]}var/maven-repo")
//        }

        ivy {
            // change to point to your repo, e.g. http://my.org/repo
//            url = uri(layout.buildDirectory.dir("repo"))
//            url = uri("c:/var/ivy-repo")
            println( File.listRoots().joinToString(","))
            val uri = uri("${File.listRoots()[0]}var/ivy-repo")
            println( "Ivy repo = ${uri.path}")
//            uri.mkdir()
            url = uri//.toURI()
        }
    }
}

//tasks.create( "x") {
//    dependsOn("printSemver")
//    val input = tasks.getByName<PrintSemverTask>( "printSemver").version.get()
//    println( input)
//}