import org.gradle.kotlin.dsl.invoke

plugins {
    id("spring-conventions")
}

tasks.bootJar {
    enabled = false
}
