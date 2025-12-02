package com.boingboing;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BounceConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue BOUNCE_FACTOR;
    public static final ModConfigSpec.DoubleValue MIN_BOUNCE_VELOCITY;
    public static final ModConfigSpec.DoubleValue HORIZONTAL_CONSERVATION;
    public static final ModConfigSpec.DoubleValue IMPACT_THRESHOLD;
    public static final ModConfigSpec.DoubleValue SOUND_VOLUME;
    public static final ModConfigSpec.IntValue PARTICLE_COUNT;
    public static final ModConfigSpec.BooleanValue ENABLE_SOUND;
    public static final ModConfigSpec.BooleanValue ENABLE_PARTICLES;

    static {
        BUILDER.push("Bounce Physics");

        BOUNCE_FACTOR = BUILDER
            .comment("Vertical bounce factor (0.0 - 3.0)")
            .comment("Higher value = higher bounce. Values > 1.0 = super bounce!")
            .defineInRange("bounceFactor", BounceConstants.DEFAULT_BOUNCE_FACTOR, 0.0, 3.0);

        MIN_BOUNCE_VELOCITY = BUILDER
            .comment("Minimum velocity to trigger a bounce")
            .comment("Below this value, the item stops")
            .defineInRange("minBounceVelocity", BounceConstants.DEFAULT_MIN_BOUNCE_VELOCITY, 0.01, 0.5);

        HORIZONTAL_CONSERVATION = BUILDER
            .comment("Horizontal velocity conservation (0.0 - 1.0)")
            .comment("1.0 = no loss, 0.0 = full stop")
            .defineInRange("horizontalConservation", BounceConstants.DEFAULT_HORIZONTAL_CONSERVATION, 0.0, 1.0);

        IMPACT_THRESHOLD = BUILDER
            .comment("Impact velocity threshold to trigger bounce")
            .defineInRange("impactThreshold", BounceConstants.DEFAULT_IMPACT_THRESHOLD, 0.01, 1.0);

        BUILDER.pop();

        BUILDER.push("Effects");

        ENABLE_SOUND = BUILDER
            .comment("Enable bounce sound")
            .define("enableSound", BounceConstants.DEFAULT_ENABLE_SOUND);

        SOUND_VOLUME = BUILDER
            .comment("Bounce sound volume (0.0 - 1.0)")
            .defineInRange("soundVolume", BounceConstants.DEFAULT_SOUND_VOLUME, 0.0, 1.0);

        ENABLE_PARTICLES = BUILDER
            .comment("Enable bounce particles")
            .define("enableParticles", BounceConstants.DEFAULT_ENABLE_PARTICLES);

        PARTICLE_COUNT = BUILDER
            .comment("Number of particles per bounce")
            .defineInRange("particleCount", BounceConstants.DEFAULT_PARTICLE_COUNT, 1, 50);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
