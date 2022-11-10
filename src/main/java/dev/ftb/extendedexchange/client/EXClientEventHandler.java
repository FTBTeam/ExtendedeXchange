package dev.ftb.extendedexchange.client;

import com.google.common.collect.EvictingQueue;
import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.client.gui.EMCFormat;
import dev.ftb.extendedexchange.config.ConfigHelper;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

@Mod.EventBusSubscriber(modid = ExtendedExchange.MOD_ID, value = Dist.CLIENT)
public class EXClientEventHandler {
    private static int timer;
    private static BigInteger emcAmount;  // tracks current player EMC level
    private static BigInteger lastEMC = BigInteger.ZERO;
    private static final int RING_BUFFER_SIZE = 5;
    @SuppressWarnings("UnstableApiUsage")
    private static final EvictingQueue<BigInteger> emcRingBuffer = EvictingQueue.create(RING_BUFFER_SIZE);
    private static final BigInteger bufferSize = BigInteger.valueOf(RING_BUFFER_SIZE);

    public static BigInteger emcRate = BigInteger.ZERO;  // rate of change of player EMC over last 5 seconds

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null) {
            // calculate change rate in player's personal EMC using a 5-second running average
            emcAmount = Minecraft.getInstance().player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)
                    .map(IKnowledgeProvider::getEmc).orElse(BigInteger.ZERO);
            if (timer == 1) {
                emcRingBuffer.add(emcAmount.subtract(lastEMC));
                lastEMC = emcAmount;

                emcRate = BigInteger.ZERO;
                for (BigInteger d : emcRingBuffer) {
                    emcRate = emcRate.add(d);
                }
                emcRate = emcRate.divide(bufferSize);

                timer = -1; //Should be -1 as this leaves the if it would increment. Toys0125
            }

            timer = (timer + 1) % 20;
        }
    }

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        emcAmount = BigInteger.ZERO;
        timer = 0;
        emcRate = BigInteger.ZERO;
        emcRingBuffer.clear();
    }

    @SubscribeEvent
    public static void addInfoText(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getInstance().player != null &&
                (!ConfigHelper.client().general.onlyShowEMCWhenHoldingModItem.get() || holdingValidItem(Minecraft.getInstance().player)))
        {
            EMCOverlayPosition oPos = ConfigHelper.client().general.screenPosition.get();
            if (oPos != EMCOverlayPosition.DISABLED && emcAmount.compareTo(BigInteger.ZERO) > 0) {
                (oPos == EMCOverlayPosition.TOP_LEFT ? event.getLeft() : event.getRight()).add("EMC: " + getEMCRateString());
            }
        }
    }

    @NotNull
    public static String getEMCRateString() {
        String s = EMCFormat.INSTANCE.format(emcAmount);
        if (emcRate.signum() != 0) {
            s += (emcRate.signum() > 0 ? (ChatFormatting.GREEN + "+") : (ChatFormatting.RED + "-")) + EMCFormat.INSTANCE.format(emcRate.abs()) + "/s";
        }
        return s;
    }

    private static boolean holdingValidItem(Player player) {
        return holdingValidItem(player.getMainHandItem()) || holdingValidItem(player.getOffhandItem());
    }
    private static boolean holdingValidItem(ItemStack stack) {
        String namespace = stack.getItem().getRegistryName().getNamespace();
        return namespace.equals(ExtendedExchange.MOD_ID) || namespace.equals(ProjectEAPI.PROJECTE_MODID);
    }
}
