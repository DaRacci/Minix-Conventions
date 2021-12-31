package dev.racci.minix.nms.player

import dev.racci.minix.nms.aliases.NMSDamageSource
import dev.racci.minix.nms.aliases.NMSEntity
import dev.racci.minix.nms.aliases.NMSPlayer
import net.minecraft.world.entity.Entity

/**
 * Alias for [Entity.awardKillScore].
 */
fun NMSPlayer.addKillScore(entity: NMSEntity, score: Int, damageSource: NMSDamageSource) {
    awardKillScore(entity, score, damageSource)
}
