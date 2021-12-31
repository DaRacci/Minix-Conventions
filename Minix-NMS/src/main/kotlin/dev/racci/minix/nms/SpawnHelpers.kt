@file:Suppress("UNUSED")
package dev.racci.minix.nms

import dev.racci.minix.nms.aliases.BukkitEntity
import dev.racci.minix.nms.aliases.NMSEntityType
import dev.racci.minix.nms.aliases.toBukkit
import dev.racci.minix.nms.aliases.toNMS
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.MobSpawnType
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason

/**
 * Spawns entity at specified Location
 *
 * Originally from [paper forums](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 *
 * @param type The type of entity to spawn *
 * @param nbtTagCompound The compound to apply to the entity
 * @param customName The custom name to give the entity
 * @param playerReference idk something
 * @param nmsSpawnType The NMS spawn type reason of the entity
 * @param ensureSpaceOrSomething If to make sure there is space to spawn the entity
 * @param spawnReason The spawn reason of the entity
 * @return Reference to the spawned bukkit Entity
 */
fun Location.spawnEntity(
    type: NMSEntityType<*>,
    nbtTagCompound: CompoundTag? = null,
    customName: Component? = null,
    playerReference: Player? = null,
    nmsSpawnType: MobSpawnType = MobSpawnType.NATURAL,
    ensureSpaceOrSomething: Boolean = true,
    spawnReason: SpawnReason = SpawnReason.DEFAULT
): BukkitEntity? {
    val nmsEntity = type.spawn(
        world.toNMS(),
        nbtTagCompound,
        customName,
        playerReference?.toNMS(),
        BlockPos(this.blockX, this.blockY, this.blockZ),
        nmsSpawnType,
        ensureSpaceOrSomething,
        false,
        spawnReason
    )
    return nmsEntity?.toBukkit()
}
