val enabled_platforms: String by project

architectury {
    common(enabled_platforms.split(","))
}