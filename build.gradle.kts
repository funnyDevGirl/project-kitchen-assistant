import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	id("application")
	id("jacoco")
	id("checkstyle")
	id("com.github.ben-manes.versions") version "0.48.0"
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	id("io.freefair.lombok") version "8.4"
}

group = "io.project.kitchen_assistant"
version = "0.0.1-SNAPSHOT"

application {
	mainClass.set("io.project.kitchen_assistant.Application")
}

checkstyle {
	configFile = file("config/checkstyle/checkstyle.xml")
	toolVersion = "10.13.0"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

	implementation("org.slf4j:slf4j-api:2.0.16")
	implementation("ch.qos.logback:logback-classic:1.5.8")
	implementation("ch.qos.logback:logback-core:1.5.8")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.json:json:20231013")
	implementation("org.commonmark:commonmark:0.18.2")
	implementation("org.jsoup:jsoup:1.15.3")

	testImplementation ("org.testcontainers:testcontainers:1.20.1")
	testImplementation("org.testcontainers:junit-jupiter:1.20.1")
	testImplementation("org.testcontainers:postgresql:1.20.2")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")
	testImplementation("org.mockito:mockito-core:5.5.0")
//	testImplementation("org.springframework:spring-mock:2.0.8")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")
	implementation("javax.validation:validation-api:1.0.0.GA")

}


tasks.withType<Test>() {
	systemProperty("file.encoding", "utf-8")
	finalizedBy(tasks.jacocoTestReport)
	useJUnitPlatform()
	testLogging {
		exceptionFormat = TestExceptionFormat.FULL
		events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
		showStandardStreams = true
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
	}
}
