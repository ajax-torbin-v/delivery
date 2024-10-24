plugins {
    id("spring-conventions")
    `java-test-fixtures`
    jacoco
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.3.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.mongock:mongock-springboot-v3:5.2.4")
    implementation("io.mongock:mongodb-springdata-v4-driver:5.2.4")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.3")
    implementation("io.github.serpro69:kotlin-faker:1.16.0")
    implementation("com.google.protobuf:protobuf-kotlin:4.28.2")
    implementation("io.nats:jnats:2.20.2")
    implementation("org.mongodb:bson:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:mongodb:1.20.1")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation(testFixtures(project(":core")))
    testFixturesImplementation(testFixtures(project(":core")))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
