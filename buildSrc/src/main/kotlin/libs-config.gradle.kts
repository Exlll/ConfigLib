plugins {
    `java-library`
}

dependencies {
    api(project(":configlib-core"))
    testImplementation(testFixtures(project(":configlib-core")))
}

