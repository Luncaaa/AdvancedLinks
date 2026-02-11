val enabled_platforms: String by project

architectury {
    common(enabled_platforms.split(","))
}

dependencies {
    implementation(project(":mod:versions:v1_21"))
    implementation(project(":mod:versions:v1_21_11"))
}