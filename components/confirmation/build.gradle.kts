val rabbitVersion: String by extra
val jacksonVersion: String by extra

dependencies {
    implementation(project(":components:rabbit-support"))
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    implementation("com.rabbitmq:amqp-client:$rabbitVersion")

    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    testImplementation(project(":components:test-support"))
}