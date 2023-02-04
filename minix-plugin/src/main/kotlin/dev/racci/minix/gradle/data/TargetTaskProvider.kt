package dev.racci.minix.gradle.data

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.DomainObjectCollection
import org.gradle.api.NamedDomainObjectCollectionSchema
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Namer
import org.gradle.api.Rule
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.util.SortedMap
import java.util.SortedSet

@Suppress("TooManyFunctions")
public class TargetTaskProvider internal constructor(
    private val target: KotlinTarget,
    private val backing: TaskContainer = target.project.tasks
) : TaskContainer by backing {
    private val targeting = Targeting(target)
    private val namer = Namer<Task> { task ->
        targeting.disambiguate(task.name)
    }

    override fun add(element: Task): Boolean {
        return backing.add(element)
    }

    override fun addAll(elements: Collection<Task>): Boolean {
        return backing.addAll(elements)
    }

    override fun addLater(provider: Provider<out Task>) {
        backing.addLater(provider)
    }

    override fun addAllLater(provider: Provider<out MutableIterable<Task>>) {
        backing.addAllLater(provider)
    }

    override fun contains(element: Task): Boolean {
        if (!backing.contains(element)) return false
        return element.name.startsWith(targeting.get())
    }

    override fun containsAll(elements: Collection<Task>): Boolean {
        return elements.all(::contains)
    }

    override fun isEmpty(): Boolean {
        return backing.none(::contains) // TODO: Probably not the best way to do this
    }

    override fun clear() {
        backing.removeAll(::contains)
    }

    override fun iterator(): MutableIterator<Task> {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun remove(element: Task?): Boolean {
        if (element == null || !contains(element)) return false
        return backing.remove(element)
    }

    override fun removeAll(elements: Collection<Task>): Boolean {
        return backing.removeAll(elements.filter(::contains).toSet())
    }

    override fun retainAll(elements: Collection<Task>): Boolean {
        val notFromThis = backing.filterNot(::contains).toSet()
        val retainFromThis = backing.filter(elements::contains).toSet()
        return backing.retainAll(retainFromThis + notFromThis)
    }

    override fun <S : Task> withType(type: Class<S>): TaskCollection<S> {
        return matchingTarget(backing.withType(type))
    }

    override fun <S : Task> withType(type: Class<S>, configureAction: Action<in S>): DomainObjectCollection<S> {
        val matching = withType(type)
        matching.configureEach(configureAction)
        return matching
    }

    override fun <S : Task> withType(type: Class<S>, configureClosure: Closure<*>): DomainObjectCollection<S> {
        throw UnsupportedOperationException("No closure support")
    }

    override fun matching(spec: Spec<in Task>): TaskCollection<Task> {
        return matchingTarget(backing.matching(spec))
    }

    override fun matching(closure: Closure<*>): TaskCollection<Task> {
        return matchingTarget(backing.matching(closure))
    }

    override fun whenObjectAdded(action: Action<in Task>): Action<in Task> {
        return matchingTarget(backing).whenObjectAdded(action)
    }

    override fun whenObjectAdded(action: Closure<*>) {
        matchingTarget(backing).whenObjectAdded(action)
    }

    override fun whenObjectRemoved(action: Action<in Task>): Action<in Task> {
        return matchingTarget(backing).whenObjectRemoved(action)
    }

    override fun whenObjectRemoved(action: Closure<*>) {
        matchingTarget(backing).whenObjectRemoved(action)
    }

    override fun all(action: Action<in Task>) {
        matchingTarget(backing).all(action)
    }

    override fun all(action: Closure<*>) {
        matchingTarget(backing).all(action)
    }

    override fun configureEach(action: Action<in Task>) {
        matchingTarget(backing).configureEach(action)
    }

    override fun findAll(spec: Closure<*>): MutableSet<Task> {
        return matchingTarget(backing).findAll(spec)
    }

    override fun getNamer(): Namer<Task> {
        return namer
    }

    override fun getAsMap(): SortedMap<String, Task> {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun getNames(): SortedSet<String> {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun findByName(name: String): Task? {
        return backing.findByName(targeting.disambiguate(name))
    }

    override fun getByName(name: String, configureClosure: Closure<*>): Task {
        return backing.getByName(targeting.disambiguate(name), configureClosure)
    }

    override fun getByName(name: String): Task {
        return backing.getByName(targeting.disambiguate(name))
    }

    override fun getByName(name: String, configureAction: Action<in Task>): Task {
        return backing.getByName(targeting.disambiguate(name), configureAction)
    }

    override fun getAt(name: String): Task {
        return backing.getAt(targeting.disambiguate(name))
    }

    override fun addRule(rule: Rule): Rule {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun addRule(description: String, ruleAction: Closure<*>): Rule {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun addRule(description: String, ruleAction: Action<String>): Rule {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun getRules(): MutableList<Rule> {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun named(name: String): TaskProvider<Task> {
        return backing.named(targeting.disambiguate(name))
    }

    override fun named(name: String, configurationAction: Action<in Task>): TaskProvider<Task> {
        return backing.named(targeting.disambiguate(name), configurationAction)
    }

    override fun <S : Task?> named(name: String, type: Class<S>): TaskProvider<S> {
        return backing.named(targeting.disambiguate(name), type)
    }

    override fun <S : Task?> named(name: String, type: Class<S>, configurationAction: Action<in S>): TaskProvider<S> {
        return backing.named(targeting.disambiguate(name), type, configurationAction)
    }

    override fun getCollectionSchema(): NamedDomainObjectCollectionSchema {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun whenTaskAdded(action: Action<in Task>): Action<in Task> {
        return whenObjectAdded(action)
    }

    override fun whenTaskAdded(closure: Closure<*>) {
        whenObjectAdded(closure)
    }

    override fun configure(configureClosure: Closure<*>): NamedDomainObjectContainer<Task> {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun create(options: MutableMap<String, *>): Task {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun create(options: MutableMap<String, *>, configureClosure: Closure<*>): Task {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun create(name: String, configureClosure: Closure<*>): Task {
        return backing.create(targeting.disambiguate(name), configureClosure)
    }

    override fun create(name: String): Task {
        return backing.create(targeting.disambiguate(name))
    }

    override fun <T : Task> create(name: String, type: Class<T>): T {
        return backing.create(targeting.disambiguate(name), type)
    }

    override fun <T : Task> create(name: String, type: Class<T>, vararg constructorArgs: Any?): T {
        return backing.create(targeting.disambiguate(name), type, *constructorArgs)
    }

    override fun <T : Task> create(name: String, type: Class<T>, configuration: Action<in T>): T {
        return backing.create(targeting.disambiguate(name), type, configuration)
    }

    override fun create(name: String, configureAction: Action<in Task>): Task {
        return backing.create(targeting.disambiguate(name), configureAction)
    }

    override fun <U : Task> maybeCreate(name: String, type: Class<U>): U {
        return backing.maybeCreate(targeting.disambiguate(name), type)
    }

    override fun maybeCreate(name: String): Task {
        return backing.maybeCreate(targeting.disambiguate(name))
    }

    override fun register(name: String, configurationAction: Action<in Task>): TaskProvider<Task> {
        return backing.register(targeting.disambiguate(name), configurationAction)
    }

    override fun <T : Task> register(
        name: String,
        type: Class<T>,
        configurationAction: Action<in T>
    ): TaskProvider<T> = backing.register(targeting.disambiguate(name), type, configurationAction)

    override fun <T : Task> register(name: String, type: Class<T>): TaskProvider<T> {
        return backing.register(targeting.disambiguate(name), type)
    }

    override fun <T : Task> register(name: String, type: Class<T>, vararg constructorArgs: Any?): TaskProvider<T> {
        return backing.register(targeting.disambiguate(name), type, *constructorArgs)
    }

    override fun register(name: String): TaskProvider<Task> {
        return backing.register(targeting.disambiguate(name))
    }

    override fun <U : Task?> containerWithType(type: Class<U>): NamedDomainObjectContainer<U> {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun findByPath(path: String): Task? {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun getByPath(path: String): Task {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun replace(name: String): Task {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override fun <T : Task> replace(name: String, type: Class<T>): T {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override val size: Int
        get() = matchingTarget(backing).size

    private fun <T : Task, C : TaskCollection<T>> matchingTarget(collection: C) = collection.matching { task ->
        task.name.startsWith(targeting.get())
    }
}
