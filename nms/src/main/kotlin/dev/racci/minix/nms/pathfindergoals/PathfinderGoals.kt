package dev.racci.minix.nms.pathfindergoals

import dev.racci.minix.nms.aliases.NMSMob
import dev.racci.minix.nms.aliases.NMSPathfinderGoal
import dev.racci.minix.nms.aliases.NMSPathfinderGoalSelector

/**
 * Adds a new [NMSPathfinderGoal] to this [NMSPathfinderGoalSelector].
 */
public inline fun NMSPathfinderGoalSelector.add(priority: Int, goal: NMSPathfinderGoal): Unit = addGoal(priority, goal)

/**
 * Adds a new [NMSPathfinderGoal] to this [NMSMob]'s Goal Selector.
 */
public fun NMSMob.addPathfinderGoal(priority: Int, goal: NMSPathfinderGoal): Unit = goalSelector.add(priority, goal)

/**
 * Removes a [NMSPathfinderGoal] from this [NMSPathfinderGoalSelector]'s Goal Selector.
 */
public fun NMSMob.removePathfinderGoal(goal: NMSPathfinderGoal): Unit = goalSelector.removeGoal(goal)

/**
 * Adds a new [NMSPathfinderGoal] to this [NMSPathfinderGoalSelector]'s Target Selector.
 */
public fun NMSMob.addTargetSelector(priority: Int, goal: NMSPathfinderGoal): Unit = targetSelector.add(priority, goal)

/**
 * Removes a [NMSPathfinderGoal] from this [NMSPathfinderGoalSelector]'s Target Selector.
 */
public fun NMSMob.removeTargetSelector(goal: NMSPathfinderGoal): Unit = targetSelector.removeGoal(goal)
