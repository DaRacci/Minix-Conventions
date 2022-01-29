@Suppress("ClassName", "ObjectPropertyName", "Unused")
/**
 * Dependencies.
 */
object Deps {

    /**
     * Dependencies for kyori adventure libs.
     */
    object adventure {
        const val api: String = "net.kyori:adventure-api"
        const val kotlin: String = "net.kyori:adventure-extra-kotlin"
        const val minimessage: String = "net.kyori:adventure-text-minimessage"
    }

    /**
     * Dependencies for minecraft related libs.
     */
    object minecraft {
        const val authLib: String = "com.mojang:authlib"
        const val acfPaper: String = "co.aikar:acf-paper"
        const val inventoryFramework: String = "com.github.stefvanschie.inventoryframework:IF"

        /**
         * Dependencies for minecraft plugin APIs.
         */
        object apis {
            const val floodgate: String = "org.geysermc.floodgate:api"
            const val protocolLib: String = "com.comphenix.protocol:ProtocolLib"
            const val placeholderAPI: String = "me.clip:placeholderapi"
        }
    }

    /**
     * Dependencies for Kotlin Exposed.
     */
    object exposed {
        const val dao: String = "org.jetbrains.exposed:exposed-dao"
        const val core: String = "org.jetbrains.exposed:exposed-core"
        const val jdbc: String = "org.jetbrains.exposed:exposed-jdbc"
        const val dateTime: String = "org.jetbrains.exposed:exposed-kotlin-datetime"
    }

    /**
     * Dependencies for Kotlin.
     */
    object kotlin {
        const val stdlib: String = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        const val reflect: String = "org.jetbrains.kotlin:kotlin-reflect"
    }

    /**
     * Dependencies for KotlinX.
     */
    object kotlinx {
        const val coroutines: String = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8"
        const val dateTime: String = "org.jetbrains.kotlinx:kotlinx-datetime-jvm"
        const val immutableCollections: String = "org.jetbrains.kotlinx:kotlinx-collections-immutable"
        const val atomicFU: String = "org.jetbrains.kotlinx:atomicfu"

        /**
         * Dependencies for KotlinX Serialization modules.
         */
        object serialization {
            const val kaml: String = "com.charleskorn.kaml:kaml"
            const val json: String = "org.jetbrains.kotlinx:kotlinx-serialization-json"
            const val cbor: String = "org.jetbrains.kotlinx:kotlinx-serialization-cbor"
            const val hocon: String = "org.jetbrains.kotlinx:kotlinx-serialization-hocon"
            const val protobuf: String = "org.jetbrains.kotlinx:kotlinx-serialization-protobuf"
            const val properties: String = "org.jetbrains.kotlinx:kotlinx-serialization-properties"
        }
    }

    /**
     * Dependencies for Logging.
     */
    object logging {
        const val sentry: String = "io.sentry:sentry"
        const val slf4jAPI: String = "org.slf4j:slf4j-api"
        const val kotlinLogger: String = "io.github.microutils:kotlin-logging-jvm"
    }

    const val sqlite_jdbc: String = "org.xerial:sqlite-jdbc"
    const val kotlin_statistics: String = "org.nield:kotlin-statistics"
    const val valiktor: String = "org.valiktor:valiktor-core"
    const val hikariCP: String = "com.zaxxer:HikariCP"

    /**
     * Dependencies for Koin.
     */
    object koin {
        private const val common = "io.insert-koin:koin"

        const val core: String = "$common-core"
        const val test: String = "$common-test"
        const val testJunit5: String = "$common-test-junit5"
        const val ktor: String = "$common-ktor"
    }

    /**
     * Dependencies for my libs / plugin APIs.
     */
    object racci {
        private const val group = "dev.racci"
        const val minix: String = "$group:Minix"
        const val minixNMS: String = "$group:Minix-nms"
        const val minixPlatform: String = "$group:Minix-Platform-Loader"
        const val raccBacc: String = "$group:RaccBacc"
    }
}
