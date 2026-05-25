import org.springframework.boot.gradle.tasks.aot.ProcessAot

plugins {
	java
	id("org.springframework.boot") version "3.5.14"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.hibernate.orm") version "6.6.13.Final"
	id("org.graalvm.buildtools.native") version "0.10.6"
	id("com.github.spotbugs") version "6.5.5"
	id("com.diffplug.spotless") version "6.25.0"
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
	runtimeOnly("org.flywaydb:flyway-database-postgresql")
	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("com.h2database:h2")
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

// SpotBugs configuration
spotbugs {
	toolVersion.set("4.9.8")
}

dependencies {
	spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0")
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask>().configureEach {
	includeFilter.set(file("build-tools/spotbugs-include.xml"))
	excludeFilter.set(file("build-tools/spotbugs-exclude.xml"))
	reports {
		maybeCreate("html").required.set(true)
	}
}

// Spotless configuration
spotless {
	java {
		target("src/main/java/**/*.java", "src/test/java/**/*.java")
		targetExclude("build/**", "src/**/generated-sources/**")
		palantirJavaFormat("2.91.0")
		removeUnusedImports()
		trimTrailingWhitespace()
		endWithNewline()
		toggleOffOn()
	}
}
