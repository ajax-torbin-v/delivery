plugins {
    id("subproject-conventions")
    `java-test-fixtures`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("systems.ajax:nats-spring-boot-starter:4.1.0.186.MASTER-SNAPSHOT")
    implementation(project(":internal-api"))
    implementation(project(":core"))
    testImplementation(testFixtures(project(":domainservice")))
    testImplementation(testFixtures(project(":core")))
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
}

tasks.test {
    useJUnitPlatform()
}
