package dev.ftb.extendedexchange.offline;

import com.google.common.collect.ImmutableSet;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Offline knowledge provider which allows EMC to be updated, but not knowledge or locks
 */
class OfflineKnowledge implements IKnowledgeProvider {
    private final UUID id;
    private final boolean fullKnowledge;
    private final Set<ItemInfo> knowledge;
    private final ItemStackHandler inputLocks = new ItemStackHandler(9);
    private BigInteger emc;

    private OfflineKnowledge(UUID id, IKnowledgeProvider toCopy) {
        this.id = id;
        this.fullKnowledge = toCopy.hasFullKnowledge();
        this.emc = toCopy.getEmc();
        this.knowledge = ImmutableSet.copyOf(toCopy.getKnowledge());
        for (int i = 0; i < toCopy.getInputAndLocks().getSlots(); i++) {
            inputLocks.setStackInSlot(i, toCopy.getInputAndLocks().getStackInSlot(i).copy());
        }
    }

    static OfflineKnowledge forPlayerId(UUID id) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return null;

        File playerData = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
        if (playerData.exists()) {
            File playerFile = new File(playerData, id.toString() + ".dat");
            if (playerFile.exists() && playerFile.isFile()) {
                try {
                    FileInputStream in = new FileInputStream(playerFile);
                    try {
                        CompoundTag playerDat = NbtIo.readCompressed(in);
                        CompoundTag data = playerDat.getCompound("ForgeCaps").getCompound(KnowledgeImpl.Provider.NAME.toString());
                        IKnowledgeProvider provider = KnowledgeImpl.getDefault();
                        provider.deserializeNBT(data);
                        return new OfflineKnowledge(id, provider);
                    } catch (IOException e) {
                        try {
                            in.close();
                        } catch (Throwable e2) {
                            e.addSuppressed(e2);
                        }
                        return null;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public boolean hasFullKnowledge() {
        return fullKnowledge;
    }

    @Override
    public void setFullKnowledge(boolean fullKnowledge) {
    }

    @Override
    public void clearKnowledge() {
    }

    @Override
    public boolean hasKnowledge(@NotNull ItemInfo itemInfo) {
        return knowledge.contains(itemInfo);
    }

    @Override
    public boolean addKnowledge(@NotNull ItemInfo itemInfo) {
        return false;
    }

    @Override
    public boolean removeKnowledge(@NotNull ItemInfo itemInfo) {
        return false;
    }

    @Override
    public @NotNull Set<ItemInfo> getKnowledge() {
        return knowledge;
    }

    @Override
    public @NotNull IItemHandler getInputAndLocks() {
        return inputLocks;
    }

    @Override
    public BigInteger getEmc() {
        return emc;
    }

    @Override
    public void setEmc(BigInteger emc) {
        this.emc = emc;
        OfflineKnowledgeManager.getInstance().onEmcChanged();
    }

    @Override
    public void sync(@NotNull ServerPlayer serverPlayer) {
    }

    @Override
    public void syncEmc(@NotNull ServerPlayer serverPlayer) {
    }

    @Override
    public void syncKnowledgeChange(@NotNull ServerPlayer serverPlayer, ItemInfo itemInfo, boolean b) {
    }

    @Override
    public void syncInputAndLocks(@NotNull ServerPlayer serverPlayer, List<Integer> list, TargetUpdateType targetUpdateType) {
    }

    @Override
    public void receiveInputsAndLocks(Map<Integer, ItemStack> map) {
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag arg) {
    }
}
