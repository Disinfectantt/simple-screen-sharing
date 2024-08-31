plugins {
    java
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.cringee"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.2")
    implementation("org.xerial:sqlite-jdbc:3.46.0.1")
    implementation("org.hibernate.orm:hibernate-community-dialects")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.springframework:spring-websocket:6.1.12")
    implementation("org.springframework:spring-messaging:6.1.12")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.12.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.cringe.simplescreensharing.SimpleScreenSharingApplication"
    }
    configurations["runtimeClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
