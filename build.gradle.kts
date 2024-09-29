plugins {
    id("java")
    id("io.github.goooler.shadow") version("8.1.8")
}

group = "me.lucaaa"
version = "1.2"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        minimize()
        relocate("net.kyori", "net.kyori")
        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    assemble {
        dependsOn(shadowJar)
    }
}