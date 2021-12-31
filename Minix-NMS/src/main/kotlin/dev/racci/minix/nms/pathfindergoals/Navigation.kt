@file:Suppress("UNUSED")
package dev.racci.minix.nms.pathfindergoals

import dev.racci.minix.nms.aliases.toNMS
import net.minecraft.world.entity.ai.navigation.PathNavigation
import org.bukkit.entity.Entity

/**
 * Whether the entity has finished navigating to its destination.
 */
val PathNavigation.doneNavigating get() = isDone

/**
 * Moves an entity to the position defined at [x], [y], [z], with a specified [speed].
 */
fun PathNavigation.moveToPosition(x: Double, y: Double, z: Double, speed: Double) = moveTo(x, y, z, speed)

/**
 * Moves to [entity], with a specified [speed].
 */
fun PathNavigation.moveToEntity(entity: Entity, speed: Double) = moveTo(entity.toNMS(), speed)

/**
 * Sets a speed multiplier with which to navigate.
 */
fun PathNavigation.setSpeed(speed: Double) = setSpeedModifier(speed)

/**
 * Stops the current navigation.
 */
fun PathNavigation.stopNavigation() = stop()
