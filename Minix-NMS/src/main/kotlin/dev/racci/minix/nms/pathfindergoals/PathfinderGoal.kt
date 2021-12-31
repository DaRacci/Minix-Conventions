@file:Suppress("UNUSED")
package dev.racci.minix.nms.pathfindergoals

import net.minecraft.world.entity.ai.goal.Goal

/**
 * Class for creating a custom Entity [Goal].
 */
abstract class PathfinderGoal: Goal() {

    /**
     * Whether the pathfinder goal should commence execution or not
     * Called every tick.
     *
     * When true is returned, calls [init], then [execute], the next tick
     * begins calling [shouldKeepExecuting] instead of [shouldExecute].
     *
     * When false is returned, nothing happens.
     *
     * @return true if should execute
     */
    abstract fun shouldExecute(): Boolean

    override fun canUse() = shouldExecute()

    /**
     * Once [shouldExecute] has returned true, [shouldKeepExecuting] will be
     * called every tick instead.
     *
     * When true is returned, [execute] will be called.
     *
     * When false is returned, [reset] will be called and
     * [shouldExecute] will start being called every tick instead.
     *
     * @return true if should keep executing.
     */
    abstract fun shouldKeepExecuting(): Boolean

    override fun isInterruptable() = shouldKeepExecuting()

    /**
     * Use to initialize the pathfinder when it starts running.
     *
     * Is called when [shouldExecute] returns true.
     */
    abstract fun init()

    override fun start() = init()

    /**
     * Use to reset the pathfinder back to its initial state.
     *
     * Is called when [shouldKeepExecuting] returns false.
     */
    abstract fun reset()

    override fun stop() = reset()

    /**
     * Is called when [shouldExecute] or [shouldKeepExecuting] return true.
     */
    abstract fun execute()

    override fun tick() = execute()
}
