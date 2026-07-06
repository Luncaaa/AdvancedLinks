architectury {
    platformSetupLoomIde()
    neoForge()
}

val modId = project.property("mod_id") as String
val modName = project.property("mod_name") as String
val modDescription = project.property("mod_description") as String
val modLicense = project.property("mod_license") as String
val neoVersion = project.property("neo_version") as String
val mcVersionRange = project.property("minecraft_version_range") as String

configurations {
    getByName("compileClasspath").extendsFrom(common)
    getByName("runtimeClasspath").extendsFrom(common)
    findByName("developmentNeoForge")?.extendsFrom(common)
}

repositories {
    maven("https://maven.neoforged.net/releases")
}

dependencies {
    "neoForge"("net.neoforged:neoforge:${neoVersion}")

    common(project(path = ":mod:v1_21:mod_common")) { isTransitive = false }

    shadowBundle(project(":common"))
    shadowBundle(project(path = ":mod:v1_21:mod_common", configuration = "transformProductionNeoForge"))
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(
                mapOf(
                    "version" to inputs.properties["version"],
                    "neo_version" to neoVersion,
                    "minecraft_version_range" to mcVersionRange,
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
    modLoaders.add("neoforge")

    modrinth {
        accessToken = System.getenv("MODRINTH_TOKEN")
        projectId = data.modrinthId
        minecraftVersions.addAll(data.versions)
    }

    curseforge {
        accessToken = System.getenv("CURSEFORGE_TOKEN")
        projectId = data.curseId
        minecraftVersions.addAll(data.versions)

        javaVersions.add(JavaVersion.VERSION_21)

        client = false
        server = true
    }
}