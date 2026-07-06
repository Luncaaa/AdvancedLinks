import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("architectury-plugin")
}

val modName = project.property("mod_name") as String
val minecraftVersion = project.property("minecraft_version") as String

architectury {
    minecraft = minecraftVersion
}

subprojects {
    plugins.apply("com.gradleup.shadow")
    plugins.apply("dev.architectury.loom")
    plugins.apply("architectury-plugin")
    plugins.apply("me.modmuss50.mod-publish-plugin")

    base {
        archivesName.set("${modName}-${project.name}-1.21.x")
    }

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()
    }

    dependencies {
        "minecraft"("net.minecraft:minecraft:${minecraftVersion}")
        val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
        "mappings"(loom.officialMojangMappings())

        implementation(project(":common"))
    }
}