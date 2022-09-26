package dev.ftb.extendedexchange;

import org.jetbrains.annotations.Nullable;

public enum Star {
    EIN("ein", "Ein", 204800000L),
    ZWEI("zwei", "Zwei", 204800000L * 4),
    DREI("drei", "Drei", 204800000L * 16),
    VIER("vier", "Vier", 204800000L * 64),
    SPHERE("sphere", "Sphere", 204800000L * 256),
    OMEGA("omega", "Omega", 204800000L * 1024);

    public static final Star[] VALUES = values();

    private final String name;
    private final String displayName;
    private final long maxEMC;

    Star(String name, String displayName, long maxEMC) {
        this.name = name;
        this.displayName = displayName;
        this.maxEMC = maxEMC;
    }

    @Nullable
    public Star getPrev() {
        return this == EIN ? null : VALUES[ordinal() - 1];
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getMaxEMC(boolean colossal) {
        return colossal ? maxEMC * 4096 : maxEMC;
    }
}
