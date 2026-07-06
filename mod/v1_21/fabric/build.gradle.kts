architectury {
    platformSetupLoomIde()
    fabric()
}

val modId = project.property("mod_id") as String
val modName = project.property("mod_name") as String
val modDescription = project.property("mod_description") as String
val modLicense = project.property("mod_license") as String
val fabricLoaderVersion = project.property("fabric_loader_version") as String
val fabricVersion = project.property("fabric_version") as String
val mcVersionRange = project.property("minecraft_version_range") as String
val papiVersion = project.property("placeholder_api_version") as String

configurations {
    getByName("compileClasspath").extendsFrom(common)
    getByName("runtimeClasspath").extendsFrom(common)
    findByName("developmentFabric")?.extendsFrom(common)
}

dependencies {
    "modImplementation"("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
    "modImplementation"("eu.pb4:placeholder-api:$papiVersion")

    common(project(path = ":mod:v1_21:mod_common")) { isTransitive = false }

    shadowBundle(project(":common"))
    shadowBundle(project(path = ":mod:v1_21:mod_common", configuration = "transformProductionFabric"))
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to inputs.properties["version"],
                    "loader_version" to fabricLoaderVersion,
                    "fabric_version" to fabricVersion,
                    "minecraft_version_range" to mcVersionRange,
                    "placeholder_api_version" to papiVersion,
                    "mod_id" to modId,
                    "mod_name" to modName,
                    "mod_description" to modDescription,
                    "mod_license" to modLicense
                )
            )
        }
    }

    shadowJar {
        configurations = listOf(project.configurations.named("shadowBundle").get())
        exclude("com/google/**", "org/jspecify/**")
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }
}

val data = rootProject.extra.get("releaseInfo") as ReleaseData
publishMods {
    file = tasks.remapJar.flatMap { it.archiveFile }
    displayName = data.name
    changelog = data.body
    type = STABLE
    modLoaders.addAll("fabric", "quilt")

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

        client = false
        server = true

        requires("fabric-api")
        optional("text-placeholder-api")
    }
}