plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    application
}

dependencies {
    implementation(project(":common"))
    implementation(project(":log-service"))
    implementation(libs.redisson)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.postgresql)
    implementation(libs.hikariCP)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

application {
    mainClass = "org.vivlaniv.nexohub.database.ApplicationKt"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}