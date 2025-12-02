plugins {
    id("com.gradleup.shadow") version("latest.release")
}

dependencies {
    implementation(project(":platform"))
    implementation(project(":platform:spigot"))
    implementation(project(":platform:folia"))
    implementation(project(":platform:common"))
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
        relocate("net.kyori", "shaded.net.kyori")
        archiveFileName.set("${project.parent?.name}-${project.version}.jar")
        destinationDirectory.set(file("../build/libs"))
    }

    assemble {
        dependsOn(shadowJar)
    }
}