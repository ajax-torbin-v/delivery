plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":internal-api"))
    implementation("io.projectreactor:reactor-core:3.6.10")
    implementation("com.salesforce.servicelibs:reactor-grpc:1.2.4")
    implementation("io.grpc:protoc-gen-grpc-java:1.68.0")
    implementation("com.salesforce.servicelibs:reactive-grpc-common:1.2.4")
    implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.2.4")
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")
    implementation("net.devh:grpc-spring-boot-starter:3.1.0.RELEASE")
    implementation("io.grpc:grpc-core:1.68.0")
    implementation("io.grpc:grpc-protobuf:1.68.0")
    implementation("io.grpc:grpc-netty:1.68.0")
    implementation("io.grpc:grpc-stub:1.68.0")
}
