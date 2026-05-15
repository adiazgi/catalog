# my-gradle-catalog

A shared Gradle [version catalog](https://docs.gradle.org/current/userguide/platforms.html)
that centralizes library and plugin versions for Kotlin / Compose Multiplatform
projects.

The catalog is published as both a Maven and an Ivy artifact, so it can be
consumed from any Gradle build via the standard `versionCatalogs { from(...) }`
mechanism.

- **Group:** `io.github.bayesiano`
- **Artifact:** `catalog`
- **Source:** `gradle/libs.versions.toml`

## Consuming the catalog

In the consumer project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/bayesiano/my-gradle-catalog")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from("io.github.bayesiano:catalog:<version>")
        }
    }
}
```

GitHub Packages requires authentication even for public packages — create a
[Personal Access Token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens)
with the `read:packages` scope and expose it through the `gpr.user` / `gpr.key`
Gradle properties (in `~/.gradle/gradle.properties`) or the `GITHUB_ACTOR` /
`GITHUB_TOKEN` environment variables.

## Building locally

```bash
./gradlew build
```

## Publishing

### To GitHub Packages

```bash
./gradlew publishAllPublicationsToGitHubPackagesRepository \
    -Pgpr.user=<your-github-user> \
    -Pgpr.key=<your-github-token-with-write:packages>
```

Or, in CI, set `GITHUB_ACTOR` and `GITHUB_TOKEN` as environment variables — the
included GitHub Actions workflow does exactly this on every tag push.

### Versioning

Versions follow [Semantic Versioning](https://semver.org/) and are derived
automatically from Git tags by the
[`javiersc.semver`](https://github.com/JavierSegoviaCordoba/semver-gradle-plugin)
plugin. Push an annotated tag like `1.2.3` to drive a release.

A version can also be forced for a single build with `-Ptag=<version>`.

## Optional: internal Maven repository

If you also need to publish to an internal Maven repository (e.g. Artifactory),
set the following Gradle properties or environment variables and the
corresponding repository will be registered automatically:

| Property                       | Environment variable      |
|--------------------------------|---------------------------|
| `urlUploadRepositoryReleases`  | `INTERNAL_MAVEN_URL`      |
| `artifactoryUsername`          | `INTERNAL_MAVEN_USER`     |
| `artifactoryPassword`          | `INTERNAL_MAVEN_PASSWORD` |

When none of these are set, only GitHub Packages is configured as a publish
target.

## License

Released under the [Apache License 2.0](LICENSE).