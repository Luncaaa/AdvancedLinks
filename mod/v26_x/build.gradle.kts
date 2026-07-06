plugins {
    id("net.neoforged.moddev") version("latest.release")
    id("me.modmuss50.mod-publish-plugin")
    id("com.gradleup.shadow")
}

val modId = project.property("mod_id") as String
val modName = project.property("mod_name") as String
val modDescription = project.property("mod_description") as String
val modLicense = project.property("mod_license") as String
val fabricLoaderVersion = project.property("fabric_loader_version") as String
val fabricVersion = project.property("fabric_version") as String
val mcVersion = project.property("minecraft_version") as String
val mcVersionRange = project.property("minecraft_version_range") as String
val papiVersion = project.property("placeholder_api_version") as String
val neoVersion = project.property("neo_version") as String

base {
    archivesName.set("${modName}-mod-26.x")
}

sourceSets {
    val main = getByName("main")

    create("fabric") {
        compileClasspath += main.output
        runtimeClasspath += main.output
        compileClasspath += files(provider { main.compileClasspath })
        runtimeClasspath += files(provider { main.runtimeClasspath })
    }

    create("neoforge") {
        compileClasspath += main.output
        runtimeClasspath += main.output
        compileClasspath += files(provider { main.compileClasspath })
        runtimeClasspath += files(provider { main.runtimeClasspath })
    }
}

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases")
}

neoForge {
    version = neoVersion

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["neoforge"])
        }
    }
}

dependencies {
    compileOnly(project(":common"))
    "fabricImplementation"(project(":common"))
    "neoforgeImplementation"(project(":common"))

    compileOnly("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    compileOnly("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    "fabricImplementation"("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    "fabricImplementation"("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    implementation("eu.pb4:placeholder-api:${papiVersion}")

    shadowBundle(project(":common"))
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
            expand(
                mapOf(
                    "version" to inputs.properties["version"],
                    "loader_version" to fabricLoaderVersion,
                    "fabric_version" to fabricVersion,
                    "neo_version" to neoVersion,
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

    compileJava {
        options.encoding = "UTF-8"
        options.release = 25
    }

    val commonProject = project(":common")

    jar {
        from(sourceSets["neoforge"].output)
        from(sourceSets["fabric"].output)
        from(commonProject.sourceSets["main"].output)
        archiveClassifier.set("raw")
        from("LICENSE") { rename { "${it}_$modId" } }
        manifest {
            attributes("Automatic-Module-Name" to "$group.$modId")
        }
    }

    shadowJar {
        dependsOn(jar)
        from(zipTree(jar.flatMap { it.archiveFile }))
        configurations = listOf(project.configurations.named("shadowBundle").get())

        exclude("com/google/**", "org/jspecify/**")
        mergeServiceFiles()
        relocate("net.kyori", "shaded.net.kyori")

        archiveClassifier.set("")
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }

    build {
        dependsOn(shadowJar)
    }
}

val data = rootProject.extra.get("releaseInfo") as ReleaseData
publishMods {
    file = tasks.shadowJar.flatMap { it.archiveFile }
    displayName = data.name
    changelog = data.body
    type = STABLE
    modLoaders.addAll("fabric", "quilt", "neoforge")

    modrinth {
        accessToken = System.getenv("MODRINTH_TOKEN")
        projectId = data.modrinthId
        minecraftVersions.addAll(data.versions26)

        requires("fabric-api")
        optional("placeholder-api")
    }

    curseforge {
        accessToken = System.getenv("CURSEFORGE_TOKEN")
        projectId = data.curseId
        minecraftVersions.addAll(data.versions26)

        javaVersions.add(JavaVersion.VERSION_25)

        client = false
        server = true

        requires("fabric-api")
        optional("text-placeholder-api")
    }
}