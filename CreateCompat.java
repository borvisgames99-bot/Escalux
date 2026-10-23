package com.qevro.escalux.compat;

import com.qevro.escalux.config.EscaluxConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import java.lang.reflect.Method;

/** Optional, reflection-only Create bridge. Escalux remains loadable without Create installed. */
public final class CreateCompat {
    private static boolean createLoaded;
    private CreateCompat() {}
    public static void initialize() { createLoaded = ModList.get().isLoaded("create"); }
    public static boolean isCreateLoaded() { return createLoaded; }
    public static boolean isBetaBridgeEnabled() {
        return createLoaded && EscaluxConfig.CREATE_INTEGRATION_BETA.get();
    }

    /** Reads the rotational speed from an adjacent Create kinetic block entity, if present. */
    public static double applyCreateSpeed(Level level, BlockPos escalatorPos, double fallback) {
        if (!isBetaBridgeEnabled()) return fallback;
        for (BlockPos check : new BlockPos[]{escalatorPos.north(), escalatorPos.south(), escalatorPos.east(), escalatorPos.west(), escalatorPos.above(), escalatorPos.below()}) {
            BlockEntity be = level.getBlockEntity(check);
            if (be == null || !be.getClass().getName().startsWith("com.simibubi.create.")) continue;
            try {
                Method getSpeed = be.getClass().getMethod("getSpeed");
                Object value = getSpeed.invoke(be);
                if (value instanceof Number number) {
                    double rpm = Math.abs(number.doubleValue());
                    if (rpm > 0.001D) return Math.max(0.02D, Math.min(0.35D, rpm / 256.0D));
                }
            } catch (ReflectiveOperationException ignored) {
                // Unsupported Create kinetic entity: keep the configured autonomous speed.
            }
        }
        return fallback;
    }
}
