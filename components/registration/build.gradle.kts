val ktorVersion: String by extra
val rabbitVersion: String by extra
val jacksonVersion: String by extra

dependencies {
    implementation(project(":components:confirmation"))
    implementation(project(":components:rabbit-support"))

    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.rabbitmq:amqp-client:$rabbitVersion")

    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    testImplementation(project(":components:test-support"))
}
