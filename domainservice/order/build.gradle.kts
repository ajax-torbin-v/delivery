plugins {
    id("subproject-conventions")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":core"))
    implementation(project(":domainservice:product"))
    implementation(project(":domainservice:user"))
    implementation(project(":domainservice:core"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.3.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("systems.ajax:nats-spring-boot-starter:4.1.0.186.MASTER-SNAPSHOT")
    implementation("systems.ajax:kafka-spring-boot-starter:3.0.3.170.MASTER-SNAPSHOT")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.3.0")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
    testImplementation("systems.ajax:nats-mock-lib:4.1.0.186.MASTER-SNAPSHOT")
    testImplementation("systems.ajax:kafka-mock:3.0.3.170.MASTER-SNAPSHOT")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("systems.ajax:nats-mock-lib:4.1.0.186.MASTER-SNAPSHOT")
    testImplementation("systems.ajax:kafka-mock:3.0.3.170.MASTER-SNAPSHOT")
    testImplementation(project(":domainservice:product"))
    testImplementation(project(":domainservice:user"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(testFixtures(project(":domainservice")))
    testImplementation(testFixtures(project(":core")))
}

tasks.test {
    useJUnitPlatform()
}
