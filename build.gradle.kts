plugins {
    kotlin("jvm") version "2.3.0-Beta1"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "me.animepdf"
version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://nexus.scarsz.me/content/groups/public/" ) {
        name = "discordsrv-repo"
    }
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.nightexpressdev.com/releases/") {
        name = "coinsengine-repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

    // DiscordSRV + JDA
    compileOnly("com.discordsrv:discordsrv:1.30.3")
    compileOnly("net.dv8tion:JDA:6.2.1") {
        exclude(module = "opus-java")
        exclude(module = "tink")
    }

    // CoinsEngine
    compileOnly("su.nightexpress.coinsengine:CoinsEngine:2.6.0")
    compileOnly("su.nightexpress.nightcore:main:2.9.4")

    // SpongePowered Configurate
    implementation("org.spongepowered:configurate-core:4.2.0")
    implementation("org.spongepowered:configurate-yaml:4.2.0") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//    implementation(kotlin("reflect"))
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    archiveClassifier = ""

    minimize()

    relocate("io.leangen.geantyref", "me.animepdf.cde.libs.geantyref")
    relocate("net.kyori.option", "me.animepdf.cde.libs.option")
    relocate("org.spongepowered.configurate", "me.animepdf.cde.libs.configurate")

    // Merge META-INF/services files where needed
    mergeServiceFiles()

    // Exclude signatures, maven/ and proguard/ from META-INF
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("META-INF/maven/**")
    exclude("META-INF/proguard/**")

    // Exclude annotations
    exclude("org/jetbrains/annotations/**")
    exclude("org/intellij/lang/annotations/**")

    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
