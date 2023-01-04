package dev.racci.minix.nms.typeinjection

import com.mojang.datafixers.DataFixer
import dev.racci.minix.nms.aliases.NMSEntity
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

public typealias NMSEntityTypeFactory<T> = EntityType.EntityFactory<T>

public typealias NMSEntityTypeBuilder = EntityType.Builder<NMSEntity>

/**
 * Returns an [NMSEntityTypeBuilder] for this specified [MobCategory].
 */
public fun NMSEntityTypeFactory<NMSEntity>.builderForCreatureType(creatureType: MobCategory): EntityType.Builder<NMSEntity> =
    EntityType.Builder.of(this, creatureType)

/**
 * NMS Data converter Registry Util.
 */
public object NMSDataConverterRegistry {

    /**
     * Returns the Data Fixer.
     */
    public fun getDataFixer(): DataFixer = DataFixers.getDataFixer()
}
