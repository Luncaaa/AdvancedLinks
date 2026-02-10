rootProject.name = "AdvancedLinks"
include("common")
include(
    "plugin",
    "platform",
    "platform:spigot",
    "platform:paper",
    "platform:folia",
    "platform:bungeecord",
    "platform:velocity",

    "mod",
    "mod:mod_common",
    "mod:fabric"
)

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://files.minecraftforge.net/maven/")
        gradlePluginPortal()
    }
}