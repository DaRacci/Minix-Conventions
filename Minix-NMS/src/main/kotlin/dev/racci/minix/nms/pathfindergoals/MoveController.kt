@file:Suppress("UNUSED")
package dev.racci.minix.nms.pathfindergoals

import net.minecraft.world.entity.ai.control.MoveControl

/**
 * Alias for [MoveControl.wantedX].
 */
val MoveControl.targetX
    get() = wantedX

/**
 * Alias for [MoveControl.wantedY].
 */
val MoveControl.targetY
    get() = wantedY

/**
 * Alias for [MoveControl.wantedZ].
 */
val MoveControl.targetZ
    get() = wantedZ

/**
 * Alias for [MoveControl.speedModifier].
 */
val MoveControl.speed
    get() = speedModifier

/**
 * Sets this entities wanted position.
 */
fun MoveControl.moveTo(x: Double, y: Double, z: Double, speed: Double) = setWantedPosition(x, y, z, speed)
