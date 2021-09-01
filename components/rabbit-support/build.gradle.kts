val rabbitVersion: String by extra

dependencies {
    implementation("com.rabbitmq:amqp-client:$rabbitVersion")

    testImplementation(project(":components:test-support"))
}
