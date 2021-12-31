@file:Suppress("UNUSED")
package dev.racci.minix.nms.typeinjection

import dev.racci.minix.nms.aliases.NMSLivingEntity
import dev.racci.minix.nms.aliases.NMSMob
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes

typealias NMSAttributeProvider = AttributeSupplier
typealias NMSAttributeBuilder = AttributeSupplier.Builder
typealias NMSGenericAttributes = Attributes

/**
 * Represents Attributes in NMS Form.
 */
object NMSAttributes {

    /**
     * Returns an Empty Builder.
     */
    fun emptyBuilder(): NMSAttributeBuilder = NMSAttributeProvider.builder()

    /**
     * Returns a builder for a Living Entity.
     */
    fun forLivingEntity(): NMSAttributeBuilder = NMSLivingEntity.createLivingAttributes()

    /**
     * Returns a builder for a Mob.
     */
    fun forMob(): NMSAttributeBuilder = NMSMob.createMobAttributes()
}

/**
 * Sets an attribute value for this builder.
 */
fun NMSAttributeBuilder.set(attribute: Attribute, value: Double? = null): NMSAttributeBuilder {
    if (value != null) {
        add(attribute, value)
    }
    return this
}
