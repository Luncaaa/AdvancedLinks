plugins {
    id("me.modmuss50.mod-publish-plugin")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val mod_id: String by project
val mod_name: String by project
val fabric_loader_version: String by project
val fabric_version: String by project
val minecraft_version_range: String by project
val placeholder_api_version: String by project

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
    findByName("developmentFabric")?.extendsFrom(common)
}

dependencies {
    "modImplementation"("net.fabricmc:fabric-loader:${fabric_loader_version}")
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
    "modImplementation"("eu.pb4:placeholder-api:$placeholder_api_version")

    common(project(path = ":mod:mod_common")) { isTransitive = false }
    common(project(path = ":mod:versions:v1_21")) { isTransitive = false }
    common(project(path = ":mod:versions:v1_21_11")) { isTransitive = false }

    shadowBundle(project(":common"))
    shadowBundle(project(path = ":mod:mod_common", configuration = "transformProductionFabric"))
    shadowBundle(project(path = ":mod:versions:v1_21", configuration = "transformProductionFabric"))
    shadowBundle(project(path = ":mod:versions:v1_21_11", configuration = "transformProductionFabric"))
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to inputs.properties["version"],
                    "loader_version" to fabric_loader_version,
                    "fabric_version" to fabric_version,
                    "minecraft_version_range" to minecraft_version_range,
                    "placeholder_api_version" to placeholder_api_version,
                    "mod_id" to mod_id,
                    "mod_name" to mod_name
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

val data = rootProject.extra["releaseInfo"] as ReleaseData
publishMods {
    file = tasks.remapJar.flatMap { it.archiveFile }
    displayName = data.name
    changelog = data.body
    type = STABLE
    modLoaders.add("fabric")

    modrinth {
        accessToken = System.getenv("MODRINTH_TOKEN")
        projectId = data.modrinthId
        minecraftVersions.addAll(data.versions)

        requires("fabric-api")
        optional("placeholder-api")
    }

    curseforge {
        accessToken = System.getenv("CURSEFORGE_TOKEN")
        projectId = data.curseId
        minecraftVersions.addAll(data.versions)

        javaVersions.add(JavaVersion.VERSION_21)

        clientRequired = false
        serverRequired = true

        requires("fabric-api")
        optional("text-placeholder-api")
    }
}