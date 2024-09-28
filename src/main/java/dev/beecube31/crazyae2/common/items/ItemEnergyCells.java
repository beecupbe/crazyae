package dev.beecube31.crazyae2.common.items;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.block.AEBaseItemBlock;
import appeng.core.Api;
import appeng.core.localization.GuiText;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.blocks.energycells.BlockAdvancedEnergyCell;
import dev.beecube31.crazyae2.common.blocks.energycells.BlockImprovedEnergyCell;
import dev.beecube31.crazyae2.common.blocks.energycells.BlockPerfectEnergyCell;
import dev.beecube31.crazyae2.common.tile.energycells.TileBigEnergyCells;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.CrazyAEConfig;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.MessageFormat;
import java.util.List;

public class ItemEnergyCells extends AEBaseItemBlock implements IAEItemPowerStorage {

    private final double maxAEPower;

    public ItemEnergyCells(final Block id) {
        super(id);
        this.maxAEPower = this.setMaxAEPower();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCheckedInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
        final NBTTagCompound tag = stack.getTagCompound();
        double internalCurrentPower = 0;

        if (maxAEPower > 0) {
            if (tag != null) {
                internalCurrentPower = tag.getDouble("internalCurrentPower");
            }

            final double percent = internalCurrentPower / maxAEPower;

            lines.add(GuiText.StoredEnergy.getLocal() + ':' + MessageFormat.format(" {0,number,#} ", internalCurrentPower) + Platform
                    .gui_localize(PowerUnits.AE.unlocalizedName) + " -" + MessageFormat.format(" {0,number,#.##%} ", percent));
        }
    }

    @Override
    public double injectAEPower(final ItemStack is, double amount, Actionable mode) {
        final double internalCurrentPower = this.getInternal(is);
        final double internalMaxPower = this.getAEMaxPower(is);
        final double required = internalMaxPower - internalCurrentPower;
        final double overflow = Math.max(0, amount - required);

        if (mode == Actionable.MODULATE) {
            final double toAdd = Math.min(required, amount);
            final double newPowerStored = internalCurrentPower + toAdd;

            this.setInternal(is, newPowerStored);
        }

        return overflow;
    }

    @Override
    public double extractAEPower(final ItemStack is, double amount, Actionable mode) {
        final double internalCurrentPower = this.getInternal(is);
        final double fulfillable = Math.min(amount, internalCurrentPower);

        if (mode == Actionable.MODULATE) {
            final double newPowerStored = internalCurrentPower - fulfillable;

            this.setInternal(is, newPowerStored);
        }

        return fulfillable;
    }

    @Override
    public double getAEMaxPower(final ItemStack is) {
        return this.maxAEPower;
    }

    public double setMaxAEPower() {
        final Block blockID = Block.getBlockFromItem(this);
        final IBlockDefinition energyCell = Api.INSTANCE.definitions().blocks().energyCell();

        return energyCell.maybeBlock().map(block ->
        {
            if (blockID instanceof BlockImprovedEnergyCell) {
                return CrazyAEConfig.impEnergyCellCap;
            } else if (blockID instanceof BlockAdvancedEnergyCell) {
                return CrazyAEConfig.advEnergyCellCap;
            } else if (blockID instanceof BlockPerfectEnergyCell) {
                return CrazyAEConfig.perEnergyCellCap;
            } else {
                return 0D;
            }
        }).orElse(0D);
    }

    @Override
    public double getAECurrentPower(final ItemStack is) {
        return this.getInternal(is);
    }

    @Override
    public AccessRestriction getPowerFlow(final ItemStack is) {
        return AccessRestriction.WRITE;
    }

    private double getInternal(final ItemStack is) {
        final NBTTagCompound nbt = Platform.openNbtData(is);
        return nbt.getDouble("internalCurrentPower");
    }

    private void setInternal(final ItemStack is, final double amt) {
        final NBTTagCompound nbt = Platform.openNbtData(is);
        nbt.setDouble("internalCurrentPower", amt);
    }
}
