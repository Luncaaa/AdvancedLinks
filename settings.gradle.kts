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
    "platform:fabric"
)

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}