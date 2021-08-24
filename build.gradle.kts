plugins {
    kotlin("jvm") version "1.5.21" apply false
}

subprojects kotlinConfig@{
    if (isNotKotlinProject()) return@kotlinConfig

    extra.apply {
        set("ktorVersion", "1.6.2")
        set("logbackVersion", "1.2.5")
    }

    group = "org.gern.marmot"

    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }

    dependencies {
        "implementation"(kotlin("stdlib"))

        "testImplementation"(kotlin("test-junit"))
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=io.ktor.locations.KtorExperimentalLocationsAPI"
        }
    }
}

fun Project.isNotKotlinProject() = name == "applications" || name == "components"
