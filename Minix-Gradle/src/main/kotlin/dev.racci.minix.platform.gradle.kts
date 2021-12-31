@Suppress("ClassName", "ObjectPropertyName", "Unused")
/**
 * Dependencies.
 */
object Deps {

    /**
     * Dependencies for kyori adventure libs.
     */
    object adventure {
        const val api = "net.kyori:adventure-api"
        const val kotlin = "net.kyori:adventure-extra-kotlin"
        const val minimessage = "net.kyori:adventure-text-minimessage"
    }

    /**
     * Dependencies for minecraft related libs.
     */
    object minecraft {
        const val authLib = "com.mojang:authlib"
        const val headLib = "de.erethon:headlib"
        const val acfPaper = "co.aikar:acf-paper"
        const val mcCoroutineAPI = "com.github.shynixn.mccoroutine:mccoroutine-bukkit-api"
        const val mcCoroutineCore = "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core"
        const val inventoryFramework = "com.github.stefvanschie.inventoryframework:IF"

        /**
         * Dependencies for minecraft plugin APIs.
         */
        object apis {
            const val floodgate = "org.geysermc.floodgate:api"
        }
    }

    /**
     * Dependencies for Kotlin Exposed.
     */
    object exposed {
        const val dao = "org.jetbrains.exposed:exposed-dao"
        const val core = "org.jetbrains.exposed:exposed-core"
        const val jdbc = "org.jetbrains.exposed:exposed-jdbc"
    }

    /**
     * Dependencies for Kotlin.
     */
    object kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect"
    }

    /**
     * Dependencies for KotlinX.
     */
    object kotlinx {
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8"

        /**
         * Dependencies for KotlinX Serialization modules.
         */
        object serialization {
            const val kaml = "com.charleskorn.kaml:kaml"
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json"
            const val cbor = "org.jetbrains.kotlinx:kotlinx-serialization-cbor"
            const val hocon = "org.jetbrains.kotlinx:kotlinx-serialization-hocon"
            const val protobuf = "org.jetbrains.kotlinx:kotlinx-serialization-protobuf"
            const val properties = "org.jetbrains.kotlinx:kotlinx-serialization-properties"
        }
    }

    const val sqlite_jdbc = "org.xerial:sqlite-jdbc"
    const val kotlin_statistics = "org.nield:kotlin-statistics"

    /**
     * Dependencies for my libs / plugin APIs.
     */
    object racci {
        private const val group = "dev.racci"
        const val minix = "$group:Minix"
        const val minix_NMS = "$group:Minix-nms"
        const val raccBacc = "$group:RaccBacc"
    }

    /**
     * Dependencies for SylphMC.
     */
    object sylph {
        private const val group = "com.sylphmc"
        const val sylphCore = "$group:Sylph"
        const val sylphEvents = "$group:SylphEvents"
    }
}
