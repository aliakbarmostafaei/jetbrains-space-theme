
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*


fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)
fun secretProperties(key: String, base64Encoded: Boolean, secretFile: String = "secrets.properties"): String {
    val file = rootProject.file(secretFile)
    if (file.exists()) {
        val properties = Properties()
        properties.load(FileInputStream(file))
        println("Looking for $key in $secretFile")
        val secret = properties.getProperty(key)

        return if (base64Encoded) {
            val decodedBytes = Base64.getDecoder().decode(secret)
            String(decodedBytes, Charsets.UTF_8)
        } else {
            secret
        }
    } else {
        throw FileNotFoundException()
    }
}

plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.gradleIntelliJPlugin) // Gradle IntelliJ Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

// Configure project's dependencies
repositories {
    mavenCentral()
}

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
//    implementation(libs.annotations)
}

// Set the JVM language level used to build the project. Use Java 11 for 2020.3+, and Java 17 for 2022.2+.
kotlin {
    jvmToolchain(17)
}

// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    type = properties("platformType")
    pluginName = properties("pluginName")
    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins = properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) }

    val platformUseLocalDist = properties("platformUseLocalDist").get().toBoolean()
    val localPlatformPath = System.getProperty("user.home") + "/Apps/idea-ce"
    if (platformUseLocalDist) {
        if (File(localPlatformPath).exists()) {
            println("Local IntelliJ IDE found in '$localPlatformPath', using to run plugin...")
            localPath = localPlatformPath
        } else {
            println("`platformUseLocalDist` was set to `true` but no instance of an IntelliJ IDE was found in '$localPlatformPath', downloading one as specified by the `platformXXX` properties")
            version = properties("platformVersion")
        }
    } else {
        version = properties("platformVersion")
    }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    patchPluginXml {
        version = properties("pluginVersion")
        sinceBuild = properties("pluginSinceBuild")
        untilBuild = properties("pluginUntilBuild")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with (it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = properties("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }
    }

    // Configure UI tests plugin
    // Read more: https://github.com/JetBrains/intellij-ui-test-robot
    runIdeForUiTests {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }

    signPlugin {
        certificateChain = environment("CERTIFICATE_CHAIN")
        if (!certificateChain.isPresent) {
            certificateChain.set(secretProperties(key="CERTIFICATE_CHAIN", base64Encoded = true))
        }

        privateKey = environment("PRIVATE_KEY")
        if (!privateKey.isPresent) {
            privateKey.set(secretProperties(key = "PRIVATE_KEY", base64Encoded = true))
        }

        password = environment("PRIVATE_KEY_PASSWORD")
        if (!password.isPresent) {
            password.set(secretProperties(key = "PRIVATE_KEY_PASSWORD", base64Encoded = false))
        }
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token = environment("PUBLISH_TOKEN")
        if (!token.isPresent) {
            token.set(secretProperties(key = "PUBLISH_TOKEN", base64Encoded = false))
        }
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = properties("pluginVersion").map { listOf(it.split('-').getOrElse(1) { "default" }.split('.').first()) }
    }
}
