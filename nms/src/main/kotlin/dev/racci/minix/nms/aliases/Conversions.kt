@file:Suppress("nothing_to_inline")

package dev.racci.minix.nms.aliases

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftCreature
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftHumanEntity
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftMob
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftSnowball
import org.bukkit.entity.Creature
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball

public typealias BukkitWorld = org.bukkit.World
public typealias NMSWorld = Level
public typealias NMSWorldServer = ServerLevel
public typealias NMSEntity = Entity
public typealias BukkitEntity = org.bukkit.entity.Entity

/** Converts the Bukkit World to its NMS Representation. */
public inline fun BukkitWorld.toNMS(): NMSWorldServer = (this as CraftWorld).handle

/** Converts the NMS World to its Bukkit Representation. */
public inline fun NMSWorld.toBukkit(): BukkitWorld = this.world

/** Converts the Bukkit Entity to its NMS Representation. */
public inline fun BukkitEntity.toNMS(): NMSEntity = (this as CraftEntity).handle

/** Converts the Bukkit Living Entity to its NMS Representation. */
public inline fun LivingEntity.toNMS(): NMSLivingEntity = (this as CraftLivingEntity).handle

/** Converts the Bukkit Mob to its NMS Representation. */
public inline fun Mob.toNMS(): NMSMob = (this as CraftMob).handle

/** Converts the Bukkit Creature to its NMS Representation. */
public inline fun Creature.toNMS(): NMSPathfindingMob = (this as CraftCreature).handle

/** Converts the Bukkit Human Entity to its NMS Representation. */
public inline fun HumanEntity.toNMS(): NMSPlayer = (this as CraftHumanEntity).handle

/** Converts the Bukkit Player to its NMS Representation. */
public inline fun Player.toNMS(): NMSServerPlayer = (this as CraftPlayer).handle

/** Converts the Bukkit Snowball to its NMS Representation. */
public inline fun Snowball.toNMS(): NMSSnowball = (this as CraftSnowball).handle

/** Converts the NMS Entity to its Bukkit Representation. */
public inline fun NMSEntity.toBukkit(): BukkitEntity = bukkitEntity

/** Converts the NMS Living Entity to its Bukkit Representation. */
public inline fun NMSLivingEntity.toBukkit(): LivingEntity = bukkitEntity as LivingEntity

/** Converts the NMS Mob to its Bukkit Representation. */
public inline fun NMSMob.toBukkit(): Mob = bukkitEntity as Mob

/** Converts the NMS Pathfinding Mob to its Bukkit Representation. */
public inline fun NMSPathfindingMob.toBukkit(): Creature = bukkitEntity as Creature

/** Converts the NMS Player to its Bukkit Representation. */
public inline fun NMSPlayer.toBukkit(): HumanEntity = bukkitEntity

/** Converts the NMS Server Player to its Bukkit Representation. */
public inline fun NMSServerPlayer.toBukkit(): Player = bukkitEntity

/** Converts the NMS Snowball to its Bukkit Representation. */
public inline fun NMSSnowball.toBukkit(): Snowball = bukkitEntity as Snowball

/** Casts the Entity to an NMS Representation. */
@JvmName("castToNMS")
@Suppress("UNCHECKED_CAST")
public inline fun <T : NMSEntity> BukkitEntity.toNMS(): T = (this as CraftEntity).handle as T
