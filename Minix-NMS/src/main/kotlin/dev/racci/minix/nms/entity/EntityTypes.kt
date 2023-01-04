package dev.racci.minix.nms.entity

import dev.racci.minix.nms.aliases.NMSCreatureType
import dev.racci.minix.nms.aliases.NMSEntityType
import dev.racci.minix.nms.aliases.toNMS
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity

/**
 * The name of the mob type as registered in Minecraft, ex. `entity.minecraft.zombie`.
 */
public val NMSEntityType<*>.keyName: String get() = descriptionId

/**
 * The type's [keyName] without the `entity.<namespace>.` prefix
 */
public val NMSEntityType<*>.typeName: String get() = typeNamespacedKey.key.replaceFirstChar(Char::uppercase)

/**
 * The [typeName] of this creature's [NMSEntityType].
 */
public val Entity.typeName: String get() = toNMS().type.typeName

/**
 * Gets a namespaced key via the NMS entity type's id.
 */
public val Entity.typeNamespacedKey: NamespacedKey get() = toNMS().type.typeNamespacedKey

/**
 * Gets the [NamespacedKey] of this.
 */
public val NMSEntityType<*>.typeNamespacedKey: NamespacedKey
    get() {
        val typeId = id
        val (namespace, key) = typeId.split(":").let {
            if (it.size == 1) {
                listOf("minecraft", typeId)
            } else it
        }
        return NamespacedKey(namespace, key)
    }

/**
 * The entity type's [NMSCreatureType].
 */
public val NMSEntityType<*>.creatureType: NMSCreatureType get() = category

/**
 * The name of the [NMSCreatureType] of this entity.
 */
public val Entity.creatureType: String get() = toNMS().type.creatureType.name

/**
 * Whether this mob's creature type (i.e. monster, creature, water_creature, ambient, misc) is [creatureType].
 */
public fun Entity.isOfCreatureType(creatureType: NMSCreatureType): Boolean = toNMS().type.creatureType.name == creatureType.name
