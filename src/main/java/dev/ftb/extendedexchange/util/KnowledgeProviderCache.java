package dev.ftb.extendedexchange.util;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Cache player's knowledge provider objects, for one tick only
 * Makes querying them in link block filter handlers a lot more efficient
 */
public enum KnowledgeProviderCache {
    INSTANCE;

    private final Map<UUID, OneTickProvider> providerMap = new HashMap<>();

    public static KnowledgeProviderCache getInstance() {
        return INSTANCE;
    }

    public IKnowledgeProvider getCachedProvider(Player player) {
        return getCachedProvider(player.getLevel(), player.getUUID());
    }

    public IKnowledgeProvider getCachedProvider(Level level, UUID id) {
        OneTickProvider otp = providerMap.get(id);
        if (otp == null || otp.timestamp < level.getGameTime()) {
            try {
                OneTickProvider newOtp = new OneTickProvider(level.getGameTime(), new WeakReference<>(ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(id)));
                providerMap.put(id, newOtp);
                return newOtp.provider().get();
            } catch (NullPointerException e) {
                // ugly, but getKnowledgeProviderFor() can throw an NPE if called immediately after player death
                return null;
            }
        } else {
            return otp.provider().get();
        }
    }

    private record OneTickProvider(long timestamp, WeakReference<IKnowledgeProvider> provider) {
    }
}
