configurations.all {
    exclude(group = "org.spigotmc", module = "spigot-api")
}

dependencies {
    compileOnly("dev.folia:folia-api:1.20.6-R0.1-SNAPSHOT")
    implementation(project(":platform:common"))
}