plugins {
    id("net.neoforged.moddev") version("latest.release")
    id("me.modmuss50.mod-publish-plugin")
}

val mod_id: String by project
val mod_name: String by project
val mod_license: String by project
val fabric_loader_version: String by project
val fabric_version: String by project
val minecraft_version: String by project
val minecraft_version_range: String by project
val placeholder_api_version: String by project
val neo_version: String by project

base {
    archivesName.set("${mod_name}-mod-26.1.x")
}

sourceSets {
    val main by getting

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

val shadowBundle by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases")
}

neoForge {
    version = neo_version

    mods {
        create(mod_id) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["neoforge"])
        }
    }
}

dependencies {
    compileOnly(project(":common"))
    "fabricImplementation"(project(":common"))
    "neoforgeImplementation"(project(":common"))

    compileOnly("net.fabricmc:fabric-loader:$fabric_loader_version")
    compileOnly("net.fabricmc.fabric-api:fabric-api:$fabric_version")
    "fabricImplementation"("net.fabricmc:fabric-loader:$fabric_loader_version")
    "fabricImplementation"("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    implementation("eu.pb4:placeholder-api:${placeholder_api_version}")

    shadowBundle(project(":common"))
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
            expand(
                mapOf(
                    "version" to inputs.properties["version"],
                    "mod_id" to mod_id,
                    "mod_name" to mod_name,
                    "mod_license" to mod_license,
                    "neo_version" to neo_version,
                    "minecraft_version_range" to minecraft_version_range,
                    "loader_version" to fabric_loader_version,
                    "fabric_version" to fabric_version,
                    "placeholder_api_version" to placeholder_api_version,
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
        from("LICENSE") { rename { "${it}_$mod_id" } }
        manifest {
            attributes("Automatic-Module-Name" to "$group.$mod_id")
        }
    }

    shadowJar {
        dependsOn(jar)
        from(zipTree(jar.flatMap { it.archiveFile }))
        configurations = listOf(shadowBundle)

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

val data = rootProject.extra["releaseInfo"] as ReleaseData
publishMods {
    file = tasks.shadowJar.flatMap { it.archiveFile }
    displayName = data.name
    changelog = data.body
    type = STABLE
    modLoaders.addAll("fabric", "neoforge")

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