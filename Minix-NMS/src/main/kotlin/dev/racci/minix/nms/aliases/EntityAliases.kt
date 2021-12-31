package dev.racci.minix.nms.aliases

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Snowball

typealias NMSMob = Mob
typealias NMSPlayer = Player
typealias NMSSnowball = Snowball
typealias NMSLivingEntity = LivingEntity
typealias NMSServerPlayer = ServerPlayer
typealias NMSPathfindingMob = PathfinderMob

typealias NMSEntityType<T> = EntityType<T>
typealias NMSCreatureType = MobCategory
