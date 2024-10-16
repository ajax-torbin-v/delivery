plugins {
    id("com.google.protobuf") version "0.9.4"
    id("delta-coverage-conventions")
    id("kotlin-conventions")
}

allprojects {
    group = "сom.example"
    version = "0.0.1-SNAPSHOT"
    repositories {
        mavenCentral()
    }
}

tasks.check {
    dependsOn(tasks.deltaCoverage)
}
