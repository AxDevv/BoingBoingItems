package com.boingboing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BounceConfigFabric {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("boingboingitem.json");

    public static double bounceFactor = BounceConstants.DEFAULT_BOUNCE_FACTOR;
    public static double minBounceVelocity = BounceConstants.DEFAULT_MIN_BOUNCE_VELOCITY;
    public static double horizontalConservation = BounceConstants.DEFAULT_HORIZONTAL_CONSERVATION;
    public static double impactThreshold = BounceConstants.DEFAULT_IMPACT_THRESHOLD;
    public static double soundVolume = BounceConstants.DEFAULT_SOUND_VOLUME;
    public static int particleCount = BounceConstants.DEFAULT_PARTICLE_COUNT;
    public static boolean enableSound = BounceConstants.DEFAULT_ENABLE_SOUND;
    public static boolean enableParticles = BounceConstants.DEFAULT_ENABLE_PARTICLES;

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                ConfigData data = GSON.fromJson(Files.readString(CONFIG_PATH), ConfigData.class);
                bounceFactor = clamp(data.bounceFactor, 0.0, 3.0);
                minBounceVelocity = clamp(data.minBounceVelocity, 0.01, 0.5);
                horizontalConservation = clamp(data.horizontalConservation, 0.0, 1.0);
                impactThreshold = clamp(data.impactThreshold, 0.01, 1.0);
                soundVolume = clamp(data.soundVolume, 0.0, 1.0);
                particleCount = clamp(data.particleCount, 1, 50);
                enableSound = data.enableSound;
                enableParticles = data.enableParticles;
            } catch (IOException e) {
                save();
            }
        } else {
            save();
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static void save() {
        ConfigData data = new ConfigData();
        data.bounceFactor = bounceFactor;
        data.minBounceVelocity = minBounceVelocity;
        data.horizontalConservation = horizontalConservation;
        data.impactThreshold = impactThreshold;
        data.soundVolume = soundVolume;
        data.particleCount = particleCount;
        data.enableSound = enableSound;
        data.enableParticles = enableParticles;

        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ConfigData {
        double bounceFactor = BounceConstants.DEFAULT_BOUNCE_FACTOR;
        double minBounceVelocity = BounceConstants.DEFAULT_MIN_BOUNCE_VELOCITY;
        double horizontalConservation = BounceConstants.DEFAULT_HORIZONTAL_CONSERVATION;
        double impactThreshold = BounceConstants.DEFAULT_IMPACT_THRESHOLD;
        double soundVolume = BounceConstants.DEFAULT_SOUND_VOLUME;
        int particleCount = BounceConstants.DEFAULT_PARTICLE_COUNT;
        boolean enableSound = BounceConstants.DEFAULT_ENABLE_SOUND;
        boolean enableParticles = BounceConstants.DEFAULT_ENABLE_PARTICLES;
    }
}
