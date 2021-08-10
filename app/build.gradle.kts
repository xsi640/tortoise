import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.2")
    implementation("org.postgresql:postgresql")
}

val bootJar: BootJar by tasks
bootJar.enabled = true