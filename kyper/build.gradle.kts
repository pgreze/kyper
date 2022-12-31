plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    `java-library`
    `maven-publish`
}

kotlin {
    explicitApi()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.pgreze"
            artifactId = project.name
            version = "WIP"

            from(components["java"])
        }
    }
}
