plugins {
    id("com.gradleup.shadow") version("latest.release")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform"))
    implementation(project(":platform:spigot"))
    implementation(project(":platform:bungeecord"))
    implementation(project(":platform:velocity"))

    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.21-R0.5-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

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
        archiveFileName.set("${project.parent?.name}-${project.version}.jar")
        destinationDirectory.set(file("../build/libs"))
    }

    assemble {
        dependsOn(shadowJar)
    }
}