plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

subprojects{
    apply(plugin = "java-library")
    repositories {
        mavenCentral()
    }
}


allprojects {
    dependencies {
        implementation("org.projectlombok:lombok:1.18.26")
        implementation("org.javatuples:javatuples:1.2")
        annotationProcessor("org.projectlombok:lombok:1.18.26")
        testImplementation(platform("org.junit:junit-bom:5.9.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }
}
tasks.test {
    useJUnitPlatform()
}