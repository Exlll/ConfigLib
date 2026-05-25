plugins {
    `core-config`
    `plugins-config`
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    api(project(":configlib-adventure"))
}

tasks.compileJava {
    dependsOn(project(":configlib-adventure").tasks.check)
}
