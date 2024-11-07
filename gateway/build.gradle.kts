plugins {
    id("spring-conventions")
    id("grpc-conventions")
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":common-proto"))
    implementation(project(":core"))
    implementation(project(":grpcapi"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.nats:jnats:2.16.14")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.projectreactor:reactor-core:3.6.10")
    testImplementation(testFixtures(project(":core")))
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesImplementation(testFixtures(project(":core")))
}
