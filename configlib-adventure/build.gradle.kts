plugins {
    `core-config`
    `libs-config`
}

val adventureVersion = "4.26.1"

dependencies {
    api(project(":configlib-core"))
    compileOnly("net.kyori:adventure-api:$adventureVersion")
    compileOnly("net.kyori:adventure-text-minimessage:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-gson:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-plain:${adventureVersion}")

    testImplementation(project(":configlib-yaml"))
    testImplementation("net.kyori:adventure-api:$adventureVersion")
    testImplementation("net.kyori:adventure-text-minimessage:$adventureVersion")
    testImplementation("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    testImplementation("net.kyori:adventure-text-serializer-gson:$adventureVersion")
    testImplementation("net.kyori:adventure-text-serializer-plain:${adventureVersion}")
}
