plugins {
    id("java")
}

allprojects {
    apply(plugin = "java")
    group = "me.lucaaa"
    version = "2.0"

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
            options.release = 21
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    dependencies {
        compileOnly("me.clip:placeholderapi:2.11.7")
        implementation("net.kyori:adventure-api:4.25.0")
        implementation("net.kyori:adventure-text-minimessage:4.25.0")
        implementation("net.kyori:adventure-text-serializer-legacy:4.25.0")
    }
}