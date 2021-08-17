import org.springframework.boot.gradle.tasks.bundling.BootJar

val vers = rootProject.extra.get("vers") as Map<String, String>

plugins {
    id("kotlin-kapt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.2")
    implementation("org.postgresql:postgresql")

    api("com.querydsl:querydsl-jpa:${vers["queryDSL"]}")
    kapt("com.querydsl:querydsl-apt:${vers["queryDSL"]}:jpa")
}

val bootJar: BootJar by tasks
bootJar.enabled = true