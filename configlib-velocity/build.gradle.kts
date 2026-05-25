plugins {
    `core-config`
    `plugins-config`
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    api(project(":configlib-adventure"))
}

tasks.compileJava {
    dependsOn(project(":configlib-adventure").tasks.check)
}
