import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.wl2027"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("cn.hutool:hutool-all:5.8.23")
    implementation("net.bytebuddy:byte-buddy:1.14.11")
    implementation("net.bytebuddy:byte-buddy-agent:1.14.11")
    implementation("org.benf:cfr:0.150")
    implementation("org.jboss.windup.decompiler:decompiler-procyon:5.3.0.Final")
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
                        "Premain-Class" to "com.runtime.pivot.agent.PreAgent",
                        "Can-Redefine-Classes" to "true",
                      )
        )
    }
}