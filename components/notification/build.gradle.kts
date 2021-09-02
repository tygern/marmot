val jacksonVersion: String by extra
val okHttpVersion: String by extra

dependencies {
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")

    testImplementation(project(":components:test-support"))
    testImplementation(project(":components:fake-sendgrid-endpoints"))
}