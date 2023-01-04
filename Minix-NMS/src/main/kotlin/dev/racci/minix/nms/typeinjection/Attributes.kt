package dev.racci.minix.nms.typeinjection

import dev.racci.minix.nms.aliases.NMSLivingEntity
import dev.racci.minix.nms.aliases.NMSMob
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes

public typealias NMSAttributeProvider = AttributeSupplier
public typealias NMSAttributeBuilder = AttributeSupplier.Builder
public typealias NMSGenericAttributes = Attributes

/**
 * Represents Attributes in NMS Form.
 */
public object NMSAttributes {

    /**
     * Returns an Empty Builder.
     */
    public fun emptyBuilder(): NMSAttributeBuilder = NMSAttributeProvider.builder()

    /**
     * Returns a builder for a Living Entity.
     */
    public fun forLivingEntity(): NMSAttributeBuilder = NMSLivingEntity.createLivingAttributes()

    /**
     * Returns a builder for a Mob.
     */
    public fun forMob(): NMSAttributeBuilder = NMSMob.createMobAttributes()
}

/**
 * Sets an attribute value for this builder.
 */
public fun NMSAttributeBuilder.set(
    attribute: Attribute,
    value: Double? = null
): NMSAttributeBuilder {
    if (value != null) {
        add(attribute, value)
    }
    return this
}
