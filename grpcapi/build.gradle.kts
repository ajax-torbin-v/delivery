import com.google.protobuf.gradle.id

plugins {
    id("kotlin-conventions")
    id("grpc-conventions")
    id("com.google.protobuf") version "0.9.4"
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.protobuf:protobuf-kotlin:4.28.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.28.2"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.68.0"
        }

        id("reactor-grpc") {
            artifact = "com.salesforce.servicelibs:reactor-grpc:1.2.4"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("reactor-grpc")
            }
        }
    }
}
