dependencies {
    api(project(mapOf("path" to ":space")))
    api(project(mapOf("path" to ":cube")))
}
tasks.test {
    useJUnitPlatform()
}