import org.gradle.api.file.DuplicatesStrategy.INCLUDE

val ktorVersion: String by extra
val logbackVersion: String by extra

dependencies {
    implementation(project(":components:fake-sendgrid-endpoints"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "org.gern.marmot.fakesendgrid.AppKt")
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
