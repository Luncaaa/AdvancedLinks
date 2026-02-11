architectury {
    platformSetupLoomIde()
    fabric()
}

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

    common(project(path = ":mod:mod_common")) {
        isTransitive = false
    }

    shadowBundle(project(path = ":mod:mod_common", configuration = "transformProductionFabric"))

    shadowBundle(project(":common"))
    shadowBundle(project(":mod:versions:v1_21")) {
        isTransitive = false
    }
    shadowBundle(project(":mod:versions:v1_21_11")) {
        isTransitive = false
    }
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
                    "placeholder_api_version" to placeholder_api_version
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
    }
}