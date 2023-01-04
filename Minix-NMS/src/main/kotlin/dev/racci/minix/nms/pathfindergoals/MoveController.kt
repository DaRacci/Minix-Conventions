package dev.racci.minix.nms.pathfindergoals

import net.minecraft.world.entity.ai.control.MoveControl

/**
 * Alias for [MoveControl.wantedX].
 */
public val MoveControl.targetX: Double
    get() = wantedX

/**
 * Alias for [MoveControl.wantedY].
 */
public val MoveControl.targetY: Double
    get() = wantedY

/**
 * Alias for [MoveControl.wantedZ].
 */
public val MoveControl.targetZ: Double
    get() = wantedZ

/**
 * Alias for [MoveControl.speedModifier].
 */
public val MoveControl.speed: Double
    get() = speedModifier

/**
 * Sets this entities wanted position.
 */
public fun MoveControl.moveTo(
    x: Double,
    y: Double,
    z: Double,
    speed: Double
): Unit = setWantedPosition(x, y, z, speed)
