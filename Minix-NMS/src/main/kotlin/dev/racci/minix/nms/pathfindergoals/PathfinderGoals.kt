@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package dev.racci.minix.nms.pathfindergoals

import dev.racci.minix.nms.aliases.NMSMob
import dev.racci.minix.nms.aliases.NMSPathfinderGoal
import dev.racci.minix.nms.aliases.NMSPathfinderGoalSelector

/**
 * Adds a new [NMSPathfinderGoal] to this [NMSPathfinderGoalSelector].
 */
inline fun NMSPathfinderGoalSelector.add(priority: Int, goal: NMSPathfinderGoal) = addGoal(priority, goal)

/**
 * Adds a new [NMSPathfinderGoal] to this [NMSMob]'s Goal Selector.
 */
fun NMSMob.addPathfinderGoal(priority: Int, goal: NMSPathfinderGoal) = goalSelector.add(priority, goal)

/**
 * Removes a [NMSPathfinderGoal] from this [NMSPathfinderGoalSelector]'s Goal Selector.
 */
fun NMSMob.removePathfinderGoal(goal: NMSPathfinderGoal) = goalSelector.removeGoal(goal)

/**
 * Adds a new [NMSPathfinderGoal] to this [NMSPathfinderGoalSelector]'s Target Selector.
 */
fun NMSMob.addTargetSelector(priority: Int, goal: NMSPathfinderGoal) = targetSelector.add(priority, goal)

/**
 * Removes a [NMSPathfinderGoal] from this [NMSPathfinderGoalSelector]'s Target Selector.
 */
fun NMSMob.removeTargetSelector(goal: NMSPathfinderGoal) = targetSelector.removeGoal(goal)
