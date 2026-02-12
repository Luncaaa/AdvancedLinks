import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.kotlin.dsl.getByName

val enabled_platforms: String by project

architectury {
    common(enabled_platforms.split(","))
}

configurations.named("minecraft") {
    dependencies.clear() // Remove the 1.21 version injected by the "mod" module
}

dependencies {
    "minecraft"("net.minecraft:minecraft:1.21.11")
    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
    "mappings"(loom.officialMojangMappings())
}