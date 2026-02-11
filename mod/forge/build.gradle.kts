architectury {
    platformSetupLoomIde()
    forge()
}

val forge_version: String by project
val forge_full_version: String by project
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
    findByName("developmentForge")?.extendsFrom(common)
}

dependencies {
    "forge"("net.minecraftforge:forge:${forge_full_version}")

    common(project(path = ":mod:mod_common")) {
        isTransitive = false
    }

    shadowBundle(project(path = ":mod:mod_common", configuration = "transformProductionForge"))

    shadowBundle(project(":common"))
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand(
                mapOf(
                    "version" to inputs.properties["version"],
                    "forge_version" to forge_version,
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
    }
}