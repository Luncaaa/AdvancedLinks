plugins {
    id("java")
    id("io.github.goooler.shadow") version("8.1.8")
}

group = "me.lucaaa"
version = "1.3"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        manifest {
            attributes(
                mapOf(
                    "paperweight-mappings-namespace" to "mojang"
                )
            )
        }

        minimize()
        relocate("net.kyori", "net.kyori")
        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    assemble {
        dependsOn(shadowJar)
    }
}