package dev.racci.minix.nms.typeinjection

import com.mojang.datafixers.DataFixer
import dev.racci.minix.nms.aliases.NMSEntity
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

typealias NMSEntityTypeFactory<T> = EntityType.EntityFactory<T>

typealias NMSEntityTypeBuilder = EntityType.Builder<NMSEntity>

/**
 * Returns an [NMSEntityTypeBuilder] for this specified [MobCategory].
 */
fun NMSEntityTypeFactory<NMSEntity>.builderForCreatureType(creatureType: MobCategory): EntityType.Builder<NMSEntity> =
    EntityType.Builder.of(this, creatureType)

/**
 * NMS Data converter Registry Util.
 */
object NMSDataConverterRegistry {

    /**
     * Returns the Data Fixer.
     */
    fun getDataFixer(): DataFixer = DataFixers.getDataFixer()
}
