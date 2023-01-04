package dev.racci.minix.nms.pathfindergoals

import net.minecraft.world.entity.ai.goal.Goal

/**
 * Class for creating a custom Entity [Goal].
 */
public abstract class PathfinderGoal : Goal() {

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
    public abstract fun shouldExecute(): Boolean

    override fun canUse(): Boolean = shouldExecute()

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
    public abstract fun shouldKeepExecuting(): Boolean

    override fun isInterruptable(): Boolean = shouldKeepExecuting()

    /**
     * Use to initialize the pathfinder when it starts running.
     *
     * Is called when [shouldExecute] returns true.
     */
    public abstract fun init()

    override fun start(): Unit = init()

    /**
     * Use to reset the pathfinder back to its initial state.
     *
     * Is called when [shouldKeepExecuting] returns false.
     */
    public abstract fun reset()

    override fun stop(): Unit = reset()

    /**
     * Is called when [shouldExecute] or [shouldKeepExecuting] return true.
     */
    public abstract fun execute()

    override fun tick(): Unit = execute()
}
