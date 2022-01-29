@file:Suppress("UNUSED")

package dev.racci.minix.nms.entity

import dev.racci.minix.nms.aliases.NMSPlayer
import dev.racci.minix.nms.aliases.toNMS
import org.bukkit.Location
import org.bukkit.entity.Entity

/**
 * @param other Another entity.
 * @return The distance between the current entity and other entity's locations.
 */
fun Entity.distanceSqrTo(
    other: Entity
): Double = distanceSqrTo(other.location)

/**
 * @param other Some location
 * @return The distance between the current entity and the other location
 */
fun Entity.distanceSqrTo(
    other: Location
): Double = location.distanceSquared(other)

/**
 * @param range the range to search within
 * @return a nearby player, or null if none are in the range
 */
fun Entity.findNearbyPlayer(
    range: Double
): NMSPlayer = world.toNMS().findNearbyPlayer(this.toNMS(), range) { true }

/**
 *  A custom definition of whether this entity should be able to reach and hit another one.
 */
fun Entity.canReach(
    target: Entity
): Boolean = distanceSqrTo(target) < reachDistance(target)

/**
 * TBH Idk.
 */
fun Entity.reachDistance(
    target: Entity
): Double = width * width + target.width * target.width
