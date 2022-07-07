import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    idea
    `maven-publish`
    id("com.github.johnrengelman.shadow")
}

val shade = configurations.create("shade")

configurations {
    compileClasspath.get().extendsFrom(shade)
}

val shadowJarTask = tasks.getByName<ShadowJar>("shadowJar") {
    configurations = listOf(shade)
    relocate("org.snakeyaml.engine", "de.exlll.configlib.org.snakeyaml.engine")
}

val coreProjectCheckTask = project(":configlib-core").tasks.getByName("check");

shadowJarTask.dependsOn(
    tasks.named("check"),
    coreProjectCheckTask
)

tasks.getByName("build").dependsOn(coreProjectCheckTask)

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.platform:junit-platform-runner:1.8.2")
    testImplementation("org.junit.platform:junit-platform-suite-api:1.8.2")
    testImplementation("org.mockito:mockito-inline:4.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.2.0")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("com.google.jimfs:jimfs:1.2")
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Exlll/ConfigLib")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    val moduleId = project.name.split("-")[1].toLowerCase()
    val publicationName = moduleId.capitalize()

    publications {
        register<MavenPublication>(publicationName) {
            from(components["java"])
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}