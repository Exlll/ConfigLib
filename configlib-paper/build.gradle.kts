plugins {
    `java-config`
}

dependencies {
    shade(project(":configlib-core"))
    implementation("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
}

tasks.jar { from(project(":configlib-core").sourceSets["main"].output) }