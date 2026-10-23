package com.qevro.escalux.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class EscaluxConfig {
    private EscaluxConfig() {}
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.BooleanValue CREATE_INTEGRATION_BETA;
    public static final ForgeConfigSpec.DoubleValue ESCALATOR_SPEED;
    public static final ForgeConfigSpec SPEC;
    static {
        BUILDER.push("general");
        CREATE_INTEGRATION_BETA = BUILDER.comment("Enable the experimental Create compatibility bridge.")
                .define("createIntegrationBeta", false);
        ESCALATOR_SPEED = BUILDER.comment("Movement speed applied to entities standing on an escalator.")
                .defineInRange("escalatorSpeed", 0.12D, 0.02D, 0.35D);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
