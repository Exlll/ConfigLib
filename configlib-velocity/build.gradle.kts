plugins {
    `java-config`
}

dependencies {
    shade(project(":configlib-core"))
    implementation("com.velocitypowered:velocity-api:3.0.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.1")
}

tasks.jar { from(project(":configlib-core").sourceSets["main"].output) }