import org.gradle.api.file.DuplicatesStrategy.INCLUDE

val rabbitVersion: String by extra
val logbackVersion: String by extra
val ktorVersion: String by extra
val jacksonVersion: String by extra
val okHttpVersion: String by extra

dependencies {
    implementation(project(":components:notification"))
    implementation(project(":components:rabbit-support"))
    implementation("com.rabbitmq:amqp-client:$rabbitVersion")

    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "org.gern.marmot.notificationserver.AppKt")
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
