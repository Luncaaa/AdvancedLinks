rootProject.name = "AdvancedLinks"
include(
    "common",
    "plugin",
    "platform",
    "platform:spigot",
    "platform:paper",
    "platform:folia",
    "platform:bungeecord",
    "platform:velocity",

    "mod",
    "mod:v1_21",
    "mod:v1_21:mod_common",
    "mod:v1_21:fabric",
    "mod:v1_21:neoforge",

    "mod:v26_1"
)

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://files.minecraftforge.net/maven/")
        gradlePluginPortal()
    }
}