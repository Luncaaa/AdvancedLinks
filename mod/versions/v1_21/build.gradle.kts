import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.kotlin.dsl.getByName

dependencies {
    "minecraft"("net.minecraft:minecraft:1.21")
    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
    "mappings"(loom.officialMojangMappings())
}