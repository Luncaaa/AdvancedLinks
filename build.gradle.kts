plugins {
    id("java")
    id("shared")
    id("com.gradleup.shadow") version("latest.release") apply false
    id("dev.architectury.loom") version("latest.release") apply false
    id("architectury-plugin") version("3.5-SNAPSHOT") apply false
    id("io.papermc.hangar-publish-plugin") version("latest.release") apply false
    id("com.modrinth.minotaur") version("latest.release") apply false
    id("me.modmuss50.mod-publish-plugin") version("latest.release") apply false
}

val mavenGroup = project.property("maven_group") as String
val modName = project.property("mod_name") as String
val modVersion = project.property("mod_version") as String

extra.set("releaseInfo", getReleaseData(modVersion))

allprojects {
    plugins.apply("java")
    plugins.apply("shared")

    group = mavenGroup
    version = modVersion

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
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
    base {
        archivesName.set("${modName}-${project.name}")
    }

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
        implementation("net.kyori:adventure-api:5.2.0")
        implementation("net.kyori:adventure-text-minimessage:5.2.0")
        implementation("net.kyori:adventure-text-serializer-legacy:5.2.0")
        implementation("net.kyori:adventure-text-serializer-gson:5.2.0")
    }
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.BIN
    }

    jar {
        enabled = false
    }

    register("publishAll") {
        group = "publishing"
        description = "Builds everything and publishes to all platforms."

        dependsOn(":plugin:publishToSites")
        dependsOn(":mod:v1_21:fabric:publishMods")
        dependsOn(":mod:v1_21:neoforge:publishMods")
        dependsOn(":mod:v26_1:publishMods")
    }
}