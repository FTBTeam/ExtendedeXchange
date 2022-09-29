package dev.ftb.extendedexchange.offline;

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
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

    public IKnowledgeProvider getCachedProvider(Level level, UUID id) {
        OneTickProvider otp = providerMap.get(id);
        if (otp == null || otp.timestamp < level.getGameTime()) {
            OneTickProvider newOtp = new OneTickProvider(level.getGameTime(), new WeakReference<>(OfflineKnowledgeManager.getInstance().getKnowledgeProviderFor(level, id)));
            providerMap.put(id, newOtp);
            return newOtp.provider().get();
        } else {
            return otp.provider().get();
        }
    }

    private record OneTickProvider(long timestamp, WeakReference<IKnowledgeProvider> provider) {
    }
}
