plugins {
    java
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:16.0.2")
    implementation("commons-codec:commons-codec:1.11") // утилиты для хеширования
    implementation("commons-io:commons-io:2.6") // утилиты для работы с IO
    implementation("commons-cli:commons-cli:1.4") // фреймворк для создания CLI
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    sourceSets {
        main {
            java.setSrcDirs(listOf("src/main"))
            resources.setSrcDirs(listOf("src/resources"))
        }
        test {
            java.setSrcDirs(listOf("src/test"))
            resources.setSrcDirs(listOf("src/testResources"))
        }
    }
}

tasks.compileJava {
    options.release.set(11)
}

tasks.test {
    useJUnitPlatform()
}
