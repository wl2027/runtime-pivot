import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.runtime.pivot"
version = "1.0.0.RELEASE"

repositories {
//    mavenCentral()
    maven {
        url = uri("https://maven.aliyun.com/repository/central")
    }
}

dependencies {
    implementation("cn.hutool:hutool-core:5.8.23")
    implementation("cn.hutool:hutool-json:5.8.23")
    implementation("org.openjdk.jol:jol-core:0.16")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    implementation("org.javassist:javassist:3.28.0-GA")
//    implementation("org.jline:jline:3.21.0")
    //implementation 'com.carrotsearch:java-sizeof:0.0.5'
    implementation("org.apache.lucene:lucene-core:8.5.0")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "8"
        targetCompatibility = "8"
        options.encoding = "UTF-8"
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
                mapOf(
                        "Premain-Class" to "com.runtime.pivot.agent.AgentMain",
                        "Can-Redefine-Classes" to "true",
                      )
        )
    }
}