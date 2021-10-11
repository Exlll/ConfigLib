plugins {
    java
    idea
    `maven-publish`
}

allprojects {
    group = "de.exlll"
    version = "2.2.0"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
    apply(plugin = "maven-publish")

    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_1_8

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.3")
        testImplementation("org.junit.platform:junit-platform-runner:1.0.3")
        testImplementation("org.junit.platform:junit-platform-suite-api:1.0.3")
        testImplementation("org.hamcrest:hamcrest-all:1.3")
        testImplementation("com.google.jimfs:jimfs:1.1")
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
            publications {
                register<MavenPublication>("gpr") {
                    from(components["java"])
                }
            }
        }
    }
}

project(":configlib-core") {
    dependencies {
        implementation("org.yaml:snakeyaml:1.20")
    }
}
project(":configlib-bukkit") {
    repositories {
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    dependencies {
        implementation(project(":configlib-core"))
        implementation("org.bukkit:bukkit:1.12.2-R0.1-SNAPSHOT")
    }

    tasks.jar { from(project(":configlib-core").sourceSets["main"].output) }
}
project(":configlib-bungee") {
    repositories {
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
    dependencies {
        implementation(project(":configlib-core"))
        implementation("net.md-5:bungeecord-api:1.12-SNAPSHOT")
    }

    tasks.jar { from(project(":configlib-core").sourceSets["main"].output) }
}