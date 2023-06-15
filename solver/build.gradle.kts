dependencies {
    api(project(mapOf("path" to ":cube")))
    implementation(project(mapOf("path" to ":removal")))
    implementation(files("../jsolid/jsolid-0.40.jar"))
}
tasks.test {
    useJUnitPlatform()
}