plugins {
    kotlin("jvm") version "1.5.21" apply false
}

subprojects kotlinConfig@{
    if (isNotKotlinProject()) return@kotlinConfig

    extra.apply {
        set("ktorVersion", "1.6.2")
        set("logbackVersion", "1.2.5")
        set("rabbitVersion", "5.13.0")
        set("jacksonVersion", "2.12.3")
        set("okHttpVersion", "4.9.1")
    }

    group = "org.gern.marmot"

    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }

    dependencies {
        "implementation"(kotlin("stdlib-jdk8"))
        "implementation"(kotlin("reflect"))
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

        "testImplementation"(kotlin("test-junit"))
        "testImplementation"("io.mockk:mockk:1.12.0")
        "testImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs +
                    "-Xopt-in=kotlin.time.ExperimentalTime"
        }
    }
}

fun Project.isNotKotlinProject() = name == "applications" || name == "components" || name == "platform-support"
