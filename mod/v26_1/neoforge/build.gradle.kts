plugins {
    id("me.modmuss50.mod-publish-plugin")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

val neo_version: String by project
val minecraft_version_range: String by project
val mod_license: String by project
val mod_id: String by project
val mod_name: String by project
val mod_version: String by project

val shadowBundle by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val common by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

configurations {
    getByName("compileClasspath").extendsFrom(common)
    getByName("runtimeClasspath").extendsFrom(common)
    findByName("developmentNeoForge")?.extendsFrom(common)
}

repositories {
    maven("https://maven.neoforged.net/releases")
}

dependencies {
    "neoForge"("net.neoforged:neoforge:${neo_version}")

    common(project(path = ":mod:v26_1:mod_common")) { isTransitive = false }

    shadowBundle(project(":common"))
    shadowBundle(project(path = ":mod:v26_1:mod_common", configuration = "transformProductionNeoForge"))
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(
                mapOf(
                    "version" to inputs.properties["version"],
                    "neo_version" to neo_version,
                    "minecraft_version_range" to minecraft_version_range,
                    "mod_license" to mod_license,
                    "mod_id" to mod_id,
                    "mod_name" to mod_name,
                    "mod_version" to mod_version
                )
            )
        }
    }

    jar {
        archiveClassifier.set("raw")
    }

    shadowJar {
        dependsOn(jar)
        exclude("com/google/**", "org/jspecify/**")
        from(zipTree(jar.flatMap { it.archiveFile }))

        configurations = listOf(shadowBundle)
        archiveClassifier.set("")
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }
}

configurations {
    named("apiElements") {
        outgoing.artifacts.clear()
        outgoing.artifact(tasks.shadowJar)
    }
    named("runtimeElements") {
        outgoing.artifacts.clear()
        outgoing.artifact(tasks.shadowJar)
    }
}

val data = rootProject.extra["releaseInfo"] as ReleaseData
publishMods {
    file = tasks.shadowJar.flatMap { it.archiveFile }
    displayName = data.name
    changelog = data.body
    type = STABLE
    modLoaders.add("neoforge")

    modrinth {
        accessToken = System.getenv("MODRINTH_TOKEN")
        projectId = data.modrinthId
        minecraftVersions.addAll(data.versions26)
    }

    curseforge {
        accessToken = System.getenv("CURSEFORGE_TOKEN")
        projectId = data.curseId
        minecraftVersions.addAll(data.versions26)

        javaVersions.add(JavaVersion.VERSION_25)

        client = false
        server = true
    }
}