plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.bytedeco:javacv-platform:1.5.11")
}

tasks {
    shadowJar {
        manifest {
            attributes["Main-Class"] = "org.example.Main" // Change this to your main class
        }
    }
}