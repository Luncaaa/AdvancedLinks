plugins {
    id("io.papermc.hangar-publish-plugin")
    id("com.modrinth.minotaur")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform"))
    implementation(project(":platform:spigot"))
    implementation(project(":platform:bungeecord"))
    implementation(project(":platform:velocity"))

    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.21-R0.5-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
}

tasks {
    shadowJar {
        manifest {
            attributes(
                mapOf(
                    "paperweight-mappings-namespace" to "mojang"
                )
            )
        }

        minimize()
        relocate("org.spongepowered.configurate", "me.lucaaa.libs.configurate")
        archiveClassifier.set("")
        destinationDirectory.set(file("../build/libs"))
    }

    assemble {
        dependsOn(shadowJar)
    }

    register("publishToSites") {
        dependsOn(publishAllPublicationsToHangar)
        dependsOn(modrinth)
    }
}

val data = rootProject.extra["releaseInfo"] as ReleaseData

hangarPublish {
    publications.register("plugin") {
        version = project.version as String
        id = "AdvancedLinks"
        channel = "Release"
        changelog = data.body

        apiKey = System.getenv("HANGAR_KEY")

        platforms {
            paper {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = listOf("1.21.x")
                dependencies {
                    hangar("PlaceholderAPI") {
                        required = false
                    }
                }
            }

            velocity {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = listOf("3.5")
            }
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("advancedlinks")
    versionNumber.set(project.version as String)
    uploadFile.set(tasks.shadowJar)
    gameVersions.addAll(data.versions)
    loaders.add("spigot")
    loaders.add("paper")
    loaders.add("purpur")
    loaders.add("folia")
    loaders.add("bungeecord")
    loaders.add("velocity")

    versionName = data.name
    changelog = data.body

    dependencies {
        optional.project("placeholderapi")
    }
}