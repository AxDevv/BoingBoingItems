package com.boingboing;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemBounceHandler {

    private final Map<UUID, Vec3d> previousVelocities = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> wasOnGround = new ConcurrentHashMap<>();

    public void onWorldTick(World world) {
        if (world.isClient()) return;
        if (!(world instanceof ServerWorld serverWorld)) return;

        serverWorld.iterateEntities().forEach(entity -> {
            if (entity instanceof ItemEntity itemEntity) {
                handleBounce(itemEntity, serverWorld);
            }
        });
    }

    private void handleBounce(ItemEntity itemEntity, ServerWorld world) {
        UUID id = itemEntity.getUuid();
        Vec3d currentVelocity = itemEntity.getVelocity();
        Vec3d prevVelocity = previousVelocities.getOrDefault(id, Vec3d.ZERO);
        boolean wasOnGroundBefore = wasOnGround.getOrDefault(id, false);

        double impactThreshold = BounceConfigFabric.impactThreshold;

        if (itemEntity.isOnGround() && !wasOnGroundBefore && prevVelocity.y < -impactThreshold) {
            double impactSpeed = Math.abs(prevVelocity.y);
            double bounceFactor = BounceConfigFabric.bounceFactor;
            double minBounceVelocity = BounceConfigFabric.minBounceVelocity;
            double horizontalConservation = BounceConfigFabric.horizontalConservation;

            double newYVelocity = impactSpeed * bounceFactor;

            if (newYVelocity > minBounceVelocity) {
                double newX = prevVelocity.x * horizontalConservation;
                double newZ = prevVelocity.z * horizontalConservation;

                itemEntity.setVelocity(newX, newYVelocity, newZ);
                itemEntity.setOnGround(false);
                itemEntity.velocityModified = true;

                if (BounceConfigFabric.enableSound) {
                    float volume = (float) (Math.min(1.0, impactSpeed * 2) * BounceConfigFabric.soundVolume);
                    float pitch = 1.0f + (float)(Math.random() * 0.2 - 0.1);
                    world.playSound(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                        SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.NEUTRAL, volume, pitch);
                }

                if (BounceConfigFabric.enableParticles) {
                    BlockPos blockBelow = BlockPos.ofFloored(itemEntity.getX(), itemEntity.getY() - 0.1, itemEntity.getZ());
                    BlockState blockState = world.getBlockState(blockBelow);

                    if (!blockState.isAir()) {
                        int particleCount = BounceConfigFabric.particleCount;
                        world.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                            itemEntity.getX(), itemEntity.getY() + 0.1, itemEntity.getZ(),
                            particleCount, 0.15, 0.05, 0.15, 0.05
                        );
                    }
                }
            }
        }

        previousVelocities.put(id, currentVelocity);
        wasOnGround.put(id, itemEntity.isOnGround());
    }
}
