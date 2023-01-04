package dev.racci.minix.nms.pathfindergoals

import dev.racci.minix.nms.aliases.toNMS
import net.minecraft.world.entity.ai.navigation.PathNavigation
import org.bukkit.entity.Entity

/**
 * Whether the entity has finished navigating to its destination.
 */
public val PathNavigation.doneNavigating: Boolean get() = isDone

/**
 * Moves an entity to the position defined at [x], [y], [z], with a specified [speed].
 */
public fun PathNavigation.moveToPosition(
    x: Double,
    y: Double,
    z: Double,
    speed: Double
): Boolean = moveTo(x, y, z, speed)

/**
 * Moves to [entity], with a specified [speed].
 */
public fun PathNavigation.moveToEntity(
    entity: Entity,
    speed: Double
): Boolean = moveTo(entity.toNMS(), speed)

/**
 * Sets a speed multiplier with which to navigate.
 */
public fun PathNavigation.setSpeed(speed: Double): Unit = setSpeedModifier(speed)

/**
 * Stops the current navigation.
 */
public fun PathNavigation.stopNavigation(): Unit = stop()
