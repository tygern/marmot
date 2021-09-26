import org.gradle.api.file.DuplicatesStrategy.INCLUDE

val ktorVersion: String by extra
val logbackVersion: String by extra
val okHttpVersion: String by extra
val rabbitVersion: String by extra

dependencies {
    implementation(project(":components:rabbit-support"))
    implementation(project(":components:confirmation"))
    implementation(project(":components:registration"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.rabbitmq:amqp-client:$rabbitVersion")

    testImplementation(project(":components:test-support"))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "io.initialcapacity.emailverifier.registrationserver.AppKt")
        }

        duplicatesStrategy = INCLUDE

        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map {
                    zipTree(it)
                }
        })
    }
}
