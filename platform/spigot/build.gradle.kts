repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(project(":platform:common"))
    implementation(project(":platform:paper"))
    implementation(project(":platform:folia"))
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
}