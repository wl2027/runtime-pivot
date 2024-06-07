plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "runtime-pivot-plugin"
include("runtime-pivot-java")
include("runtime-pivot-core")
