plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    application
}

dependencies {
    implementation(project(":common"))
    implementation(project(":log-service"))
    implementation(libs.redisson)
    implementation(libs.kotlin.faker)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

application {
    mainClass= "org.vivlaniv.nexohub.model.ApplicationKt"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}