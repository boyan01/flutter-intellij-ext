import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    kotlin("jvm") version "1.3.72"
}

group = "tech.soit"
version = "1.0-SNAPSHOT"

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1.1"
    setPlugins("dart:201.7223.99", "io.flutter:47.0.3", "yaml")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
}


dependencies {
    implementation(kotlin("stdlib-jdk7"))
}


repositories {
    jcenter()
    mavenCentral()
}

tasks.getByName<PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
        """
      Add change notes here.<br>
      <em>most HTML tags may be used</em>"""
    )
    pluginDescription(file("${rootProject.projectDir}/description.md").readText())
}