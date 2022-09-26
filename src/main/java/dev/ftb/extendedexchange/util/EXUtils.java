package dev.ftb.extendedexchange.util;

import dev.ftb.extendedexchange.ExtendedExchange;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class EXUtils {
    public static final Direction[] DIRECTIONS = Direction.values();

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(ExtendedExchange.MOD_ID, path);
    }

    public static int mod(int i, int n) {
        i = i % n;
        return i < 0 ? i + n : i;
    }

}
