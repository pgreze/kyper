plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    jacoco
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin")
}

val myGroup = "com.github.pgreze"
    .also { group = it }
val myArtifactId = "kyper"
val tagVersion = System.getenv("GITHUB_REF")?.split('/')?.last()
val myVersion = (tagVersion?.trimStart('v') ?: "WIP")
    .also { version = it }
val myDescription = "Functional Kotlin friendly way to create command line applications."
    .also { description = it }
val githubUrl = "https://github.com/pgreze/$myArtifactId"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withSourcesJar()
    withJavadocJar()
}

kotlin {
    explicitApi()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    disabledRules.set(setOf("import-ordering"))
}

jacoco {
    toolVersion = "0.8.7"
}
tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(System.getenv("CI") != "true")
    }
}

dependencies {
    implementation(Kotlin.stdlib)
    implementation(kotlin("reflect"))
    implementation(KotlinX.coroutines.core)

    testImplementation(Testing.junit.jupiter.engine)
    testImplementation(Testing.junit.jupiter.params)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    // https://kotest.io/docs/assertions/assertions.html
    testImplementation(Testing.kotest.assertions.core)
}

//
// Publishing
//

val propOrEnv: (String, String) -> String? = { key, envName ->
    project.properties.getOrElse(key, defaultValue = { System.getenv(envName) })?.toString()
}

val ossrhUsername = propOrEnv("ossrh.username", "OSSRH_USERNAME")
val ossrhPassword = propOrEnv("ossrh.password", "OSSRH_PASSWORD")

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = myGroup
            artifactId = myArtifactId
            version = myVersion

            from(components["java"])

            pom {
                name.set(myArtifactId)
                description.set(myDescription)
                url.set(githubUrl)
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("pgreze")
                        name.set("Pierrick Greze")
                    }
                }
                scm {
                    connection.set("$githubUrl.git")
                    developerConnection.set("scm:git:ssh://github.com:pgreze/$myArtifactId.git")
                    url.set(githubUrl)
                }
            }
        }
    }
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    sign(publishing.publications)
}

nexusPublishing {
    packageGroup.set(myGroup)
    repositories {
        sonatype {
            username.set(ossrhUsername)
            password.set(ossrhPassword)
        }
    }
}
