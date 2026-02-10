plugins {
    id("java")
    id("com.gradleup.shadow") version("latest.release")
}

val maven_group: String by project
val mod_version: String by project

allprojects {
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")

    group = maven_group
    version = mod_version

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
            options.release = 21
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://maven.nucleoid.xyz/")
    }

    dependencies {
        compileOnly("me.clip:placeholderapi:2.12.2")
        implementation("net.kyori:adventure-api:4.26.1")
        implementation("net.kyori:adventure-text-minimessage:4.26.1")
        implementation("net.kyori:adventure-text-serializer-legacy:4.26.1")
        implementation("net.kyori:adventure-text-serializer-bungeecord:4.4.1")
    }
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.BIN
    }
}