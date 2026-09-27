package com.github.tacowasa059.sphericalplayermod.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_CONFIG;

    public static final ForgeConfigSpec.IntValue SEGMENTS;

    static {
        BUILDER.push("Client Settings for Spherical Player Mod");

        SEGMENTS = BUILDER.comment("The number of segments of a sphere. Must be a natural number that is a multiple of 4 and up to 32.")
                .defineInRange("segments", 16, 8, 32);

        BUILDER.pop();
        CLIENT_CONFIG = BUILDER.build();
    }
}

