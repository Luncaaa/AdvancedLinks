plugins {
    id("com.modrinth.minotaur")
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

    common(project(path = ":mod:mod_common")) { isTransitive = false }
    common(project(path = ":mod:versions:v1_21")) { isTransitive = false }
    common(project(path = ":mod:versions:v1_21_11")) { isTransitive = false }

    shadowBundle(project(":common"))
    shadowBundle(project(path = ":mod:mod_common", configuration = "transformProductionNeoForge"))
    shadowBundle(project(path = ":mod:versions:v1_21", configuration = "transformProductionNeoForge"))
    shadowBundle(project(path = ":mod:versions:v1_21_11", configuration = "transformProductionNeoForge"))
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

    shadowJar {
        configurations = listOf(shadowBundle)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
        destinationDirectory.set(file("../../build/libs"))
    }
}

val data = rootProject.extra["releaseInfo"] as ReleaseData
modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(data.modrinthId)
    versionNumber.set(project.version as String)
    uploadFile.set(tasks.remapJar)
    gameVersions.addAll(data.versions)
    loaders.add("neoforge")

    versionName = data.name
    changelog = data.body
}