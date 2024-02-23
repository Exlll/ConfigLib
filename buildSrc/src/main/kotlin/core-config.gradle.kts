plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
    idea
}

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
    testFixturesApi("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testFixturesApi("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testFixturesApi("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testFixturesApi("org.junit.platform:junit-platform-runner:1.10.0")
    testFixturesApi("org.junit.platform:junit-platform-suite-api:1.10.0")
    testFixturesApi("org.mockito:mockito-inline:5.2.0")
    testFixturesApi("org.mockito:mockito-junit-jupiter:5.6.0")
    testFixturesApi("org.hamcrest:hamcrest-all:1.3")
    testFixturesApi("com.google.jimfs:jimfs:1.3.0")
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

    val moduleId = project.name.split("-")[1].lowercase()
    val publicationName = moduleId.replaceFirstChar(Char::titlecase)

    publications {
        register<MavenPublication>(publicationName) {
            from(components["java"])

            pom {

                name = "ConfigLib ${publicationName}"
                description = "A Minecraft library for saving, loading, updating, " +
                        "and commenting YAML configuration files."
                url = "https://github.com/Exlll/ConfigLib"

                developers {
                    developer {
                        name = "Exlll"
                        email = "exlll321@gmail.com"
                        url = "https://github.com/Exlll"
                    }
                }

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/license/mit"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/Exlll/ConfigLib.git"
                    developerConnection = "scm:git:ssh://github.com:Exlll/ConfigLib.git"
                    url = "https://github.com/Exlll/ConfigLib/tree/master"
                }
            }
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) {
    skip()
}
