dependencies {
    api(project(mapOf("path" to ":space")))
}
tasks.test {
    useJUnitPlatform()
}