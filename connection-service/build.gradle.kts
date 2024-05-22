plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    application
}

dependencies {
    implementation(project(":common"))
    implementation(project(":log-service"))
    implementation(libs.redisson)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.org.eclipse.paho.client.mqttv3)
    implementation(libs.jbcrypt)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

application {
    mainClass= "org.vivlaniv.nexohub.ApplicationKt"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}