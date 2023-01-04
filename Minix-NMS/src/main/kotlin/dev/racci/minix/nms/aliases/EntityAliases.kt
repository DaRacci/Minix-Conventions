package dev.racci.minix.nms.aliases

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Snowball

public typealias NMSMob = Mob
public typealias NMSPlayer = Player
public typealias NMSSnowball = Snowball
public typealias NMSLivingEntity = LivingEntity
public typealias NMSServerPlayer = ServerPlayer
public typealias NMSPathfindingMob = PathfinderMob

public typealias NMSEntityType<T> = EntityType<T>
public typealias NMSCreatureType = MobCategory
