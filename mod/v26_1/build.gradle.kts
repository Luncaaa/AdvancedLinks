plugins {
    id("dev.architectury.loom-no-remap") version("latest.release") apply false
    id("architectury-plugin") version("3.5-SNAPSHOT")
}

val minecraft_version: String by project

architectury {
    minecraft = minecraft_version
}

subprojects {
    apply(plugin = "dev.architectury.loom-no-remap")
    apply(plugin = "architectury-plugin")

    dependencies {
        "minecraft"("net.minecraft:minecraft:${minecraft_version}")

        implementation(project(":common"))
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
            options.release = 25
        }
    }
}