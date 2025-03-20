package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.definitions.IItemDefinition;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.core.sync.GuiBridge;
import appeng.helpers.Reflected;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.misc.PartInterface;
import appeng.util.Platform;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.duality.PerfectInterfaceDuality;
import dev.beecube31.crazyae2.common.interfaces.gui.IPriHostExtender;
import dev.beecube31.crazyae2.common.interfaces.gui.IPriHostGuiOverrider;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.jetbrains.annotations.NotNull;

public class PartPerfectInterface extends PartInterface implements IPriHostGuiOverrider, IPriHostExtender, IUpgradesInfoProvider {

    public static final ResourceLocation MODEL_BASE = new ResourceLocation(Tags.MODID, "part/perfect_iface_base");

    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/interface_off"));

    @PartModels
    public static final PartModel MODELS_ON = new PartModel(MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/interface_on"));

    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/interface_has_channel"));

    @Reflected
    public PartPerfectInterface(final ItemStack is) {
        super(is);
        ObfuscationReflectionHelper.setPrivateValue(PartInterface.class, this, new PerfectInterfaceDuality(this.getProxy(), this), "duality");
    }

    @Override
    public @NotNull IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    @Override
    public CrazyAEGuiBridge getOverrideGui() {
        return CrazyAEGuiBridge.PERFECT_INTERFACE;
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return CrazyAE.definitions().blocks().perfectInterface().maybeStack(1).orElse(ItemStack.EMPTY);
    }

    @Override
    public GuiBridge getGuiBridge() {
        return GuiBridge.GUI_Handler;
    }

    @Override
    public IItemDefinition getBlock() {
        return CrazyAE.definitions().parts().perfectInterface();
    }

    @Override
    public boolean onPartActivate(final EntityPlayer player, final EnumHand hand, final Vec3d pos) {
        if (Platform.isServer()) {
            CrazyAEGuiHandler.openGUI(player, this.getHost().getTile(), this.getSide(), this.getOverrideGui());
        }
        return true;
    }

    @Override
    public int getConfigSlots() {
        return 36;
    }

    @Override
    public int getStorageSlots() {
        return 36;
    }

    @Override
    public int getPatternsSlots() {
        return 0;
    }
}
