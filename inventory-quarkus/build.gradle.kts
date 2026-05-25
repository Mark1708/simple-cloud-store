plugins {
    java
    id("io.quarkus")
    id("com.github.spotbugs") version "6.5.5"
    id("com.diffplug.spotless") version "6.25.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-resteasy")
    implementation("io.quarkus:quarkus-resteasy-jsonb")

    implementation("io.quarkus:quarkus-arc")

    implementation("io.quarkus:quarkus-hibernate-orm-panache")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-flyway")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.quarkus:quarkus-jdbc-h2")
    testImplementation("io.rest-assured:rest-assured")
}

group = "cloud.store"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
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
