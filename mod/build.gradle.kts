import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("dev.architectury.loom") version("latest.release") apply false
    id("architectury-plugin") version("3.4-SNAPSHOT")
}

val minecraft_version: String by project

architectury {
    minecraft = minecraft_version
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()
    }

    dependencies {
        "minecraft"("net.minecraft:minecraft:${minecraft_version}")
        val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
        "mappings"(loom.officialMojangMappings())

        implementation(project(":common"))

        implementation("net.kyori:adventure-text-serializer-gson:4.26.1")
    }
}