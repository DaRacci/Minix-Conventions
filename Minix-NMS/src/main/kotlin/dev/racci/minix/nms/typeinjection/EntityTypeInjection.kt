@file:Suppress("UNUSED", "UNCHECKED_CAST")

package dev.racci.minix.nms.typeinjection

import com.mojang.datafixers.DSL
import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import dev.racci.minix.nms.aliases.NMSEntity
import dev.racci.minix.nms.aliases.NMSEntityType
import net.minecraft.SharedConstants
import net.minecraft.core.DefaultedRegistry
import net.minecraft.core.MappedRegistry
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.entity.EntityType

typealias NMSRegistry<T> = MappedRegistry<T>
typealias NMSNamespacedKey = ResourceLocation

/**
 * NMS Registry Wrapper.
 */
object NMSRegistryWrapper {

    /**
     * Returns the default ENTITY_TYPE registry.
     */
    val ENTITY_TYPE: DefaultedRegistry<EntityType<*>> = NMSRegistry.ENTITY_TYPE
}

/**
 * Registers an [NMSEntityType] with the server.
 */
fun <T : NMSEntity> NMSEntityType<T>.registerEntityType(
    namespace: String,
    key: String
): NMSEntityType<T> = NMSRegistry.register(NMSRegistryWrapper.ENTITY_TYPE, NMSNamespacedKey(namespace, key), this)

/**
 * Injects an entity into the server
 *
 * Originally from [paper forums](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 */
fun NMSEntityTypeBuilder.injectType(
    namespace: String,
    key: String,
    extendFrom: String
): NMSEntityType<NMSEntity> {
    val nameKey = "$namespace:$key"
    val dataTypes = NMSDataConverterRegistry.getDataFixer()
        .getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().dataVersion.version))
        .findChoiceType(NMSDataConverterTypesWrapper.ENTITY).types() as MutableMap<String, Type<*>>
    if (dataTypes.containsKey(nameKey)) println("ALREADY CONTAINS KEY: $key")
    dataTypes[nameKey] = dataTypes[extendFrom]!!

    return build(nameKey).registerEntityType(namespace, key)
}

/**
 * NMS Data converter types wrapper.
 */
object NMSDataConverterTypesWrapper {

    /**
     * Type reference for ENTITY.
     */
    val ENTITY: DSL.TypeReference = References.ENTITY
}
