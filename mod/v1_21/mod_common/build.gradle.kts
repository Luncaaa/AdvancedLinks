val enabledPlatforms = project.property("enabled_platforms") as String

architectury {
    common(enabledPlatforms.split(","))
}