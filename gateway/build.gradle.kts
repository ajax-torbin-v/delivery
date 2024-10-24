plugins {
    id("spring-conventions")
    jacoco
    `java-test-fixtures`
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.nats:jnats:2.16.14")
    implementation("io.projectreactor:reactor-core:3.6.10")
    testImplementation(testFixtures(project(":core")))
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesImplementation(testFixtures(project(":core")))
}
