package com.boingboing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemBounceHandler {

    private final Map<UUID, Vec3> previousVelocities = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> wasOnGround = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            previousVelocities.put(itemEntity.getUUID(), Vec3.ZERO);
            wasOnGround.put(itemEntity.getUUID(), false);
        }
    }

    @SubscribeEvent
    public void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            previousVelocities.remove(itemEntity.getUUID());
            wasOnGround.remove(itemEntity.getUUID());
        }
    }

    @SubscribeEvent
    public void onServerTick(net.minecraftforge.event.TickEvent.LevelTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.START) return;
        if (event.level.isClientSide()) return;
        if (!(event.level instanceof ServerLevel serverLevel)) return;

        serverLevel.getEntities().getAll().forEach(entity -> {
            if (entity instanceof ItemEntity itemEntity) {
                handleBounce(itemEntity, serverLevel);
            }
        });
    }

    private void handleBounce(ItemEntity itemEntity, ServerLevel level) {
        UUID id = itemEntity.getUUID();
        Vec3 currentVelocity = itemEntity.getDeltaMovement();
        Vec3 prevVelocity = previousVelocities.getOrDefault(id, Vec3.ZERO);
        boolean wasOnGroundBefore = wasOnGround.getOrDefault(id, false);

        double impactThreshold = BounceConfig.IMPACT_THRESHOLD.get();

        if (itemEntity.onGround() && !wasOnGroundBefore && prevVelocity.y < -impactThreshold) {
            double impactSpeed = Math.abs(prevVelocity.y);
            double bounceFactor = BounceConfig.BOUNCE_FACTOR.get();
            double minBounceVelocity = BounceConfig.MIN_BOUNCE_VELOCITY.get();
            double horizontalConservation = BounceConfig.HORIZONTAL_CONSERVATION.get();

            double newYVelocity = impactSpeed * bounceFactor;

            if (newYVelocity > minBounceVelocity) {
                double newX = prevVelocity.x * horizontalConservation;
                double newZ = prevVelocity.z * horizontalConservation;

                itemEntity.setDeltaMovement(newX, newYVelocity, newZ);
                itemEntity.setOnGround(false);
                itemEntity.hasImpulse = true;

                if (BounceConfig.ENABLE_SOUND.get()) {
                    float volume = (float) (Math.min(1.0, impactSpeed * 2) * BounceConfig.SOUND_VOLUME.get());
                    float pitch = 1.0f + (float)(Math.random() * 0.2 - 0.1);
                    level.playSound(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                        SoundEvents.SLIME_SQUISH_SMALL, SoundSource.NEUTRAL, volume, pitch);
                }

                if (BounceConfig.ENABLE_PARTICLES.get()) {
                    BlockPos blockBelow = BlockPos.containing(itemEntity.getX(), itemEntity.getY() - 0.1, itemEntity.getZ());
                    BlockState blockState = level.getBlockState(blockBelow);

                    if (!blockState.isAir()) {
                        int particleCount = BounceConfig.PARTICLE_COUNT.get();
                        level.sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                            itemEntity.getX(), itemEntity.getY() + 0.1, itemEntity.getZ(),
                            particleCount, 0.15, 0.05, 0.15, 0.05
                        );
                    }
                }
            }
        }

        previousVelocities.put(id, currentVelocity);
        wasOnGround.put(id, itemEntity.onGround());
    }
}
