package dev.racci.minix.nms.entity

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

/**
 * Sets what this entity is looking at.
 */
public fun Entity.lookAt(x: Double, y: Double, z: Double) {
    val dirBetweenLocations = Vector(x, y, z).subtract(location.toVector())
    val location = location
    location.direction = dirBetweenLocations
    setRotation(location.yaw, location.pitch)
}

/**
 * Sets the entities location it looks at.
 */
public fun Entity.lookAt(location: Location): Unit = lookAt(location.x, location.y, location.z)

/**
 * Makes this entity look at another entity.
 */
public fun Entity.lookAt(entity: Entity): Unit = lookAt(entity.location)

/**
 * Sets the [x] and [z] that the entity is looking at with its current y level.
 */
public fun Entity.lookAt(
    x: Double,
    z: Double
): Unit = lookAt(x, location.y, z)

/**
 * Sets the entities pitch that it looks at to this [location].
 */
public fun Entity.lookAtPitchLock(location: Location): Unit = lookAt(location.x, location.z)

/**
 * Sets the entities pitch that it looks at to this [entity]'s pitch.
 */
public fun Entity.lookAtPitchLock(entity: Entity): Unit = lookAtPitchLock(entity.location)
