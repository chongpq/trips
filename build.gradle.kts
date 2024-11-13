plugins {
    id("java")
    application
    jacoco
}

group = "com.littlepay"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "com.littlepay.Main"
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jar {
    manifest {
        attributes(mapOf("Main-Class" to application.mainClass))
    }
}
