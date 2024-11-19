plugins {
    id("subproject-conventions")
}

dependencies {
    implementation("io.mongock:mongodb-springdata-v4-driver:5.4.4")
    implementation("io.mongock:mongock-springboot-v3:5.2.4")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation(project(":domainservice:order"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
