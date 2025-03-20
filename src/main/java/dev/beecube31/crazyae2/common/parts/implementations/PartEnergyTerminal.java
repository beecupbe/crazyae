package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PartEnergyTerminal extends CrazyAEAbstractPartTerminal {

    @PartModels
    public static final ResourceLocation MODEL_BASE = new ResourceLocation(Tags.MODID, "part/energy/energy_terminal");
    @PartModels
    public static final ResourceLocation MODEL_OFF = new ResourceLocation(Tags.MODID, "part/energy/energy_terminal_off");
    @PartModels
    public static final ResourceLocation MODEL_ON = new ResourceLocation(Tags.MODID, "part/energy/energy_terminal_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public PartEnergyTerminal(ItemStack is) {
        super(is);
    }

    @Override
    public CrazyAEGuiBridge getGui(EntityPlayer player) {
        return CrazyAEGuiBridge.GUI_ENERGY_TERMINAL;
    }

    @Override
    @NotNull
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }
}
