plugins {
    `java-library`
    `java-test-fixtures`
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

tasks.named<Jar>("jar") {
    enabled = true
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("io.github.oshai:kotlin-logging:7.0.12")
    api("io.jsonwebtoken:jjwt-api:0.12.6")
    api("com.linecorp.kotlin-jdsl:jpql-dsl:3.5.5")
    api("com.linecorp.kotlin-jdsl:jpql-render:3.5.5")
    api("com.linecorp.kotlin-jdsl:spring-data-jpa-support:3.5.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    testFixturesImplementation("org.springframework:spring-test")
    testFixturesImplementation("io.kotest:kotest-runner-junit5:6.1.3")
    testImplementation("io.kotest:kotest-runner-junit5:6.1.3")
    testImplementation("io.mockk:mockk:1.14.2")
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.2.9.Final:${if (System.getProperty("os.arch") == "aarch64") "osx-aarch_64" else "osx-x86_64"}")
}
