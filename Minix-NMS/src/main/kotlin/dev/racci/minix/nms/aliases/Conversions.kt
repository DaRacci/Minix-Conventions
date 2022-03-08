@file:Suppress("NOTHING_TO_INLINE", "UNUSED", "UNCHECKED_CAST")

package dev.racci.minix.nms.aliases

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftCreature
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftHumanEntity
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftMob
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftSnowball
import org.bukkit.entity.Creature
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball

typealias BukkitWorld = org.bukkit.World
typealias NMSWorld = Level
typealias NMSWorldServer = ServerLevel
typealias NMSEntity = Entity
typealias BukkitEntity = org.bukkit.entity.Entity

/**
 * Converts the Bukkit World to its NMS Representation.
 */
inline fun BukkitWorld.toNMS(): NMSWorldServer = (this as CraftWorld).handle

/**
 * Converts the NMS World to its Bukkit Representation.
 */
inline fun NMSWorld.toBukkit(): BukkitWorld = this.world

/**
 * Converts the Bukkit Entity to its NMS Representation.
 */
inline fun BukkitEntity.toNMS(): NMSEntity = (this as CraftEntity).handle

/**
 * Converts the Bukkit Living Entity to its NMS Representation.
 */
inline fun LivingEntity.toNMS(): NMSLivingEntity = (this as CraftLivingEntity).handle

/**
 * Converts the Bukkit Mob to its NMS Representation.
 */
inline fun Mob.toNMS(): NMSMob = (this as CraftMob).handle

/**
 * Converts the Bukkit Creature to its NMS Representation.
 */
inline fun Creature.toNMS(): NMSPathfindingMob = (this as CraftCreature).handle

/**
 * Converts the Bukkit Human Entity to its NMS Representation.
 */
inline fun HumanEntity.toNMS(): NMSPlayer = (this as CraftHumanEntity).handle

/**
 * Converts the Bukkit Player to its NMS Representation.
 */
inline fun Player.toNMS(): NMSServerPlayer = (this as CraftPlayer).handle

/**
 * Converts the Bukkit Snowball to its NMS Representation.
 */
inline fun Snowball.toNMS(): NMSSnowball = (this as CraftSnowball).handle

/**
 * Converts the NMS Entity to its Bukkit Representation.
 */
inline fun NMSEntity.toBukkit(): BukkitEntity = bukkitEntity

/**
 * Converts the NMS Living Entity to its Bukkit Representation.
 */
inline fun NMSLivingEntity.toBukkit(): LivingEntity = bukkitEntity as LivingEntity

/**
 * Converts the NMS Mob to its Bukkit Representation.
 */
inline fun NMSMob.toBukkit(): Mob = bukkitEntity as Mob

/**
 * Converts the NMS Pathfinding Mob to its Bukkit Representation.
 */
inline fun NMSPathfindingMob.toBukkit(): Creature = bukkitEntity as Creature

/**
 * Converts the NMS Player to its Bukkit Representation.
 */
inline fun NMSPlayer.toBukkit(): HumanEntity = bukkitEntity

/**
 * Converts the NMS Server Player to its Bukkit Representation.
 */
inline fun NMSServerPlayer.toBukkit(): Player = bukkitEntity

/**
 * Converts the NMS Snowball to its Bukkit Representation.
 */
inline fun NMSSnowball.toBukkit(): Snowball = bukkitEntity as Snowball

/**
 * Casts the Entity to an NMS Representation.
 */
@JvmName("toNMSWithCast")
inline fun <T : NMSEntity> org.bukkit.entity.Entity.toNMS(): T = (this as CraftEntity).handle as T
