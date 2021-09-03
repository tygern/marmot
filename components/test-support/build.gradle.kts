val ktorVersion: String by extra
val jacksonVersion: String by extra
val rabbitVersion: String by extra

dependencies {
    api("io.ktor:ktor-server-core:$ktorVersion")
    api("io.ktor:ktor-server-jetty:$ktorVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.rabbitmq:amqp-client:$rabbitVersion")
    implementation(kotlin("test-junit"))

    implementation(project(":components:rabbit-support"))
}