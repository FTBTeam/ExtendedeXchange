package dev.ftb.extendedexchange.util;

import dev.ftb.extendedexchange.ExtendedExchange;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class EXUtils {
    public static final Direction[] DIRECTIONS = Direction.values();

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(ExtendedExchange.MOD_ID, path);
    }

    public static int mod(int i, int n) {
        i = i % n;
        return i < 0 ? i + n : i;
    }

    public static KnowledgeAddResult addKnowledge(Player player, IKnowledgeProvider knowledgeProvider, ItemStack stack) {
        if (stack.isEmpty() || !ProjectEAPI.getEMCProxy().hasValue(stack)) {
            return KnowledgeAddResult.NOT_ADDED;
        }

        if (!knowledgeProvider.hasKnowledge(stack)) {
            ItemInfo info = ItemInfo.fromStack(stack);
            ItemInfo cleaned = ProjectEAPI.getEMCProxy().getPersistentInfo(info);
            if (MinecraftForge.EVENT_BUS.post(new PlayerAttemptLearnEvent(player, info, cleaned))) {
                return KnowledgeAddResult.NOT_ADDED;
            }
            return knowledgeProvider.addKnowledge(stack) ? KnowledgeAddResult.ADDED : KnowledgeAddResult.NOT_ADDED;
        }

        return KnowledgeAddResult.ALREADY_KNOWN;
    }

    public static boolean playerHasKnowledge(Player player, ItemStack stack) {
        return ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(player.getUUID()).hasKnowledge(stack);
    }

    public enum KnowledgeAddResult {
        NOT_ADDED,
        ALREADY_KNOWN,
        ADDED
    }
}
