import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel

plugins {
    java
    idea
    `maven-publish`
    id("net.neoforged.moddev") version "2.0.74"
}

val minecraftVersion: String by project
val minecraftVersionRange: String by project
val neoVersion: String by project
val neoVersionRange: String by project
val loaderVersionRange: String by project

val modId: String by project
val modName: String by project
val modLicense: String by project
val modVersion: String by project
val modGroupId: String by project
val modAuthors: String by project
val modDescription: String by project

val rhinoVersion: String by project
val kubejsVersion: String by project

repositories {
    mavenLocal()
    maven("https://maven.latvian.dev/releases")
    maven("https://www.cursemaven.com")
    maven {
        url = uri("https://maven.latvian.dev/releases")
        content {
            includeGroup("dev.latvian.mods")
            includeGroup("dev.latvian.apps")
        }
    }
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.rtyley")
        }
    }
}

base {
    archivesName = modId
    version = modVersion
    group = modGroupId
}

neoForge {
    version = neoVersion
    validateAccessTransformers = true

    runs {
        register("gameTestServer") {
            server()
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }
        register("client") {
            client()
        }
        register("data") {
            data()
        }
        register("server") {
            server()
        }
        configureEach {
            jvmArgument("-XX:+IgnoreUnrecognizedVMOptions")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
            if (type.get() == "client") {
                programArguments.addAll("--width", "1920", "--height", "1080")
            }
        }
    }

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

dependencies {
    implementation(accessTransformers(interfaceInjectionData("dev.latvian.mods:kubejs-neoforge:$kubejsVersion")!!)!!)

    implementation("dev.latvian.mods:rhino:$rhinoVersion")
    implementation("curse.maven:jei-238222:7229074")

//    runtimeOnly("curse.maven:emi-580555:6205506")
//    runtimeOnly("curse.maven:tmrv-1194921:6269681")
    runtimeOnly("curse.maven:jade-324717:5591256")
    runtimeOnly("curse.maven:probejs-585406:7105159")
}

tasks {
    processResources {
        val replaceProperties = mapOf(
            "minecraft_version" to minecraftVersion,
            "minecraft_version_range" to minecraftVersionRange,
            "neo_version" to neoVersion,
            "neo_version_range" to neoVersionRange,
            "loader_version_range" to loaderVersionRange,
            "mod_id" to modId,
            "mod_name" to modName,
            "mod_license" to modLicense,
            "mod_version" to modVersion,
            "mod_authors" to modAuthors,
            "mod_description" to modDescription,
            "kubejs_version" to kubejsVersion
        )

        inputs.properties(replaceProperties)
        filesMatching(listOf("META-INF/neoforge.neoforge.mods.toml")) {
            expand(replaceProperties)
        }
    }
    compileJava {
        options.encoding = "UTF-8"
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components.getByName("java"))
        }
    }
    repositories {
        maven("file://$projectDir/repo")
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
    project {
        jdkName = "${java.sourceCompatibility}"
        languageLevel = IdeaLanguageLevel(java.sourceCompatibility)
    }
}
