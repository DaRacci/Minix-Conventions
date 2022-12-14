val serverVersion: String by project
val useTentacles: String? by project

plugins {
    java
}

repositories {
    maven("https://repo.racci.dev/snapshots")
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    val (major, minor) = serverVersion.split('.').take(2).map { it.takeWhile(Char::isDigit).toInt() }
    val groupAndModule = if (useTentacles.toBoolean()) {
        "dev.racci.tentacles:tentacles-api"
    } else {
        // Use old purpurmc groupId with versions below 1.18
        "${if (major == 1 && minor < 18) "net.pl3x.purpur" else "org.purpurmc.purpur"}:purpur-api"
    }
    compileOnly("$groupAndModule:$serverVersion")
}
