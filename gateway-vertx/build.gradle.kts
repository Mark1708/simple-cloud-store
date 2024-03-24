import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("org.graalvm.buildtools.native") version "0.9.28"
}

group = "cloud.store"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.4.8"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "cloud.store.gateway.GatewayVerticle"
val launcherClassName = "io.vertx.core.Launcher"

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
