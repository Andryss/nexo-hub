plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    application
}

dependencies {
    implementation(libs.redisson)
    implementation(libs.clickhouse.http.client)
    implementation(libs.httpclient5)
    implementation(libs.lz4.java)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.logback.classic)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

application {
    mainClass= "org.vivlaniv.nexohub.log.ApplicationKt"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}