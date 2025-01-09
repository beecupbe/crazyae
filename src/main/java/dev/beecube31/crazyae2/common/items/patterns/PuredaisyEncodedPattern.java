package dev.beecube31.crazyae2.common.items.patterns;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import appeng.helpers.InvalidPatternHelper;
import appeng.util.Platform;
import appeng.util.item.ItemStackHashStrategy;
import dev.beecube31.crazyae2.common.util.patterns.crafting.PuredaisyCraftingPatternDetails;
import dev.beecube31.crazyae2.core.CrazyAE;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

public class PuredaisyEncodedPattern extends ItemCustomEncodedPatternBase implements ICraftingPatternItem {

    private static final ItemStackHashStrategy hashStrategy = ItemStackHashStrategy.comparingAllButCount();
    protected static final Map<ItemStack, ItemStack> SIMPLE_CACHE = new Object2ObjectOpenCustomHashMap<>(hashStrategy);

    @Override
    public ICraftingPatternDetails getPatternForItem(final ItemStack is, final World w) {
        try {
            return new PuredaisyCraftingPatternDetails(is);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    public ItemStack getOutput(ItemStack item) {
        ItemStack out = SIMPLE_CACHE.get(item);

        if (out != null) {
            return out;
        }

        final World w = AppEng.proxy.getWorld();
        if (w == null) {
            return ItemStack.EMPTY;
        }

        final ICraftingPatternDetails details = this.getPatternForItem(item, w);

        out = details != null ? details.getOutputs()[0].createItemStack() : ItemStack.EMPTY;

        SIMPLE_CACHE.put(item, out);
        return out;
    }

    @Override
    protected boolean clearPattern(final ItemStack stack, final EntityPlayer player) {
        if (player.isSneaking()) {
            SIMPLE_CACHE.remove(stack);
            if (Platform.isClient()) {
                return false;
            }

            final InventoryPlayer inv = player.inventory;

            ItemStack is = CrazyAE.definitions().materials().puredaisyBlankPattern().maybeStack(stack.getCount()).orElse(ItemStack.EMPTY);
            if (!is.isEmpty()) {
                for (int s = 0; s < player.inventory.getSizeInventory(); s++) {
                    if (inv.getStackInSlot(s) == stack) {
                        inv.setInventorySlotContents(s, is);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addCheckedInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
        final ICraftingPatternDetails details = this.getPatternForItem(stack, world);

        if (details == null) {
            if (!stack.hasTagCompound()) {
                return;
            }

            stack.setStackDisplayName(TextFormatting.RED + GuiText.InvalidPattern.getLocal());

            InvalidPatternHelper invalid = new InvalidPatternHelper(stack);

            final String label = (invalid.isCraftable() ? GuiText.Crafts.getLocal() : GuiText.Creates.getLocal()) + ": ";
            final String and = ' ' + GuiText.And.getLocal() + ' ';
            final String with = GuiText.With.getLocal() + ": ";

            boolean first = true;
            for (final InvalidPatternHelper.PatternIngredient output : invalid.getOutputs()) {
                lines.add((first ? label : and) + output.getFormattedToolTip());
                first = false;
            }

            first = true;
            for (final InvalidPatternHelper.PatternIngredient input : invalid.getInputs()) {
                lines.add((first ? with : and) + input.getFormattedToolTip());
                first = false;
            }

            if (invalid.isCraftable()) {
                final String substitutionLabel = GuiText.Substitute.getLocal() + " ";
                final String canSubstitute = invalid.canSubstitute() ? GuiText.Yes.getLocal() : GuiText.No.getLocal();

                lines.add(substitutionLabel + canSubstitute);
            }

            return;
        }

        if (stack.hasDisplayName()) {
            stack.removeSubCompound("display");
        }

        final IAEItemStack[] in = details.getCondensedInputs();
        final IAEItemStack[] out = details.getCondensedOutputs();

        final String label = GuiText.Crafts.getLocal() + ": ";
        final String and = ' ' + GuiText.And.getLocal() + ' ';
        final String with = GuiText.With.getLocal() + ": ";

        boolean first = true;
        for (final IAEItemStack anOut : out) {
            if (anOut == null) {
                continue;
            }

            lines.add((first ? label : and) + anOut.getStackSize() + ' ' + Platform.getItemDisplayName(anOut));
            first = false;
        }

        first = true;
        for (final IAEItemStack anIn : in) {
            if (anIn == null) {
                continue;
            }

            lines.add((first ? with : and) + anIn.getStackSize() + ' ' + Platform.getItemDisplayName(anIn));
            first = false;
        }
    }
}
