package dev.racci.minix.nms // ktlint-disable filename

import dev.racci.minix.nms.aliases.BukkitEntity
import dev.racci.minix.nms.aliases.NMSEntityType
import dev.racci.minix.nms.aliases.toBukkit
import dev.racci.minix.nms.aliases.toNMS
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.MobSpawnType
import org.bukkit.Location
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason

/**
 * Spawns entity at specified Location
 *
 * @param type The type of entity to spawn.
 * @param nbtTagCompound The compound to apply to the entity.
 * @param nmsSpawnType The NMS spawn type reason of the entity
 * @param spawnReason The spawn reason of the entity
 * @param alignPosition Align the position of the entity to the block position. (I think)
 * @param invertY Invert the Y position of the entity. (Only used if [alignPosition] is true. | The fuck does this do?)
 * @param f The function that is run after the entity has been spawned.
 *
 * @return Reference to the spawned bukkit Entity
 */
public fun Location.spawnEntity(
    type: NMSEntityType<*>,
    nbtTagCompound: CompoundTag? = null,
    nmsSpawnType: MobSpawnType = MobSpawnType.NATURAL,
    spawnReason: SpawnReason = SpawnReason.DEFAULT,
    alignPosition: Boolean,
    invertY: Boolean,
    f: (BukkitEntity) -> Unit = {}
): BukkitEntity? = type.spawn(
    world.toNMS(),
    nbtTagCompound,
    { f(it.toBukkit()) },
    BlockPos(this.blockX, this.blockY, this.blockZ),
    nmsSpawnType,
    alignPosition,
    invertY,
    spawnReason
)?.toBukkit()
