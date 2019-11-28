import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

val protobufVersion by extra { "3.10.0" }
val grpcVersion by extra { "1.25.0" }

plugins {
    kotlin("jvm") version "1.3.61"
    id("com.google.protobuf") version "0.8.10"

    `java-library`
    `maven-publish`
    idea
}

group = "dev.baesangwoo"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")
    implementation("com.google.protobuf:protobuf-java-util:$protobufVersion")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri("s3://the-maven-repository/releases")

            val awsAccessKey: String by project
            val awsSecretKey: String by project

            credentials(AwsCredentials::class) {
                accessKey = awsAccessKey
                secretKey = awsSecretKey
            }
        }
    }
}
