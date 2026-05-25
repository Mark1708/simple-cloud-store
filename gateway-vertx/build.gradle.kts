import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("org.graalvm.buildtools.native") version "0.10.6"
  id("com.github.spotbugs") version "6.5.5"
  id("com.diffplug.spotless") version "6.25.0"
}

group = "cloud.store"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "5.0.12"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "cloud.store.gateway.GatewayVerticle"
val launcherClassName = "io.vertx.launcher.application.VertxApplication"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web")

  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-config")

  implementation("io.vertx:vertx-rx-java3")

  implementation("io.vertx:vertx-web-client")

  implementation("io.vertx:vertx-health-check")

  implementation("io.vertx:vertx-launcher-application")

  implementation("org.slf4j:slf4j-api:2.0.12")
  implementation("org.slf4j:slf4j-simple:2.0.12")

  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

graalvmNative {
  binaries {
    named("main") {
      imageName.set("gateway")
      mainClass.set(launcherClassName)
      buildArgs.add("-H:IncludeResources=\".*/vertx-version.txt\"")
      buildArgs.add("--initialize-at-build-time=org.slf4j.helpers.Reporter")
      buildArgs.add("--initialize-at-build-time=org.slf4j.LoggerFactory")
      buildArgs.add("--initialize-at-build-time=org.slf4j.simple.SimpleLogger")
    }
    named("test") {
    }
  }
  binaries.all {
    buildArgs.add("--verbose")
  }
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}

// SpotBugs configuration
spotbugs {
  toolVersion.set("4.9.8")
}

dependencies {
  spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0")
}

tasks.withType<SpotBugsTask>().configureEach {
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
