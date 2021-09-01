val ktorVersion: String by extra
val rabbitVersion: String by extra

dependencies {
    implementation(project(":components:rabbit-support"))

    implementation("io.ktor:ktor-locations:$ktorVersion")

    implementation("com.rabbitmq:amqp-client:$rabbitVersion")
}
