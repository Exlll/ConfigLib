import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(project(":configlib-yaml"))
}

tasks.shadowJar {
    relocate("org.snakeyaml.engine", "de.exlll.configlib.org.snakeyaml.engine")
}

tasks.compileJava {
    dependsOn(
        project(":configlib-core").tasks.check,
        project(":configlib-yaml").tasks.check
    )
}

val javaComponent = components["java"] as AdhocComponentWithVariants
val shadowRuntimeElementsConfiguration = configurations["shadowRuntimeElements"]

javaComponent.withVariantsFromConfiguration(shadowRuntimeElementsConfiguration) {
    skip()
}