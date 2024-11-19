plugins {
    id("subproject-conventions")
    `java-test-fixtures`
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("systems.ajax:nats-spring-boot-starter:4.1.0.186.MASTER-SNAPSHOT")
    implementation(project(":internal-api"))
    implementation(project(":core"))
    implementation("org.springframework.data:spring-data-redis:3.3.0")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
    implementation("io.github.resilience4j:resilience4j-reactor:2.2.0")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.3.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.3.0")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(testFixtures(project(":domainservice")))
    testImplementation(testFixtures(project(":core")))
}

tasks.test {
    useJUnitPlatform()
}
