plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight)
    alias(libs.plugins.runpaper)
}

group = "dev.tarna"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public")
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
    implementation(libs.bundles.cloud)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.mongodb)
    compileOnly(libs.invui)
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveBaseName.set("MoreTweaks")
        archiveClassifier.set("")
    }

    runServer {
        minecraftVersion("1.21.4")
        jvmArgs("-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true")

        downloadPlugins {
            modrinth("viaversion", "5.4.0-SNAPSHOT+747")
        }
    }
}