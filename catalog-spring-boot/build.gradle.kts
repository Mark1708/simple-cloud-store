import org.springframework.boot.gradle.tasks.aot.ProcessAot

plugins {
	java
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.hibernate.orm") version "6.4.4.Final"
	id("org.graalvm.buildtools.native") version "0.9.28"
}

group = "cloud.store"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.flywaydb:flyway-core")
	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

hibernate {
	enhancement {
		enableAssociationManagement.set(true)
	}
}

graalvmNative {
	binaries {
		named("main") {
			imageName.set("catalog")
			mainClass.set("cloud.store.catalog.CatalogApplication")
		}
		named("test") {
		}
	}
	binaries.all {
		buildArgs.add("--verbose")
	}
}

tasks.withType<ProcessAot> {
	args("--spring.profiles.active=" + (project.properties["aotProfiles"] ?: "default"))
}