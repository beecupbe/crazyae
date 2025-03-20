package dev.beecube31.crazyae2.common.util.patterns.crafting;

import appeng.api.AEApi;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.common.interfaces.crafting.ICrazyAECraftingPatternDetails;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static appeng.helpers.ItemStackHelper.stackFromNBT;

public abstract class CrazyAECraftingPatternDetails implements ICrazyAECraftingPatternDetails {
    protected int priority = 0;
    protected IAEItemStack[] inputs;
    protected IAEItemStack[] outputs;
    protected IAEItemStack pattern;
    protected final ItemStack patternItem;

    public CrazyAECraftingPatternDetails
    (
            final ItemStack is
    )
    {
        final NBTTagCompound encodedValue = is.getTagCompound();
        if (encodedValue == null) {
            throw new IllegalArgumentException("Invalid pattern provided : " + is);
        }

        final NBTTagList inTag = encodedValue.getTagList("input", 10);
        final NBTTagList outTag = encodedValue.getTagList("output", 10);

        final List<IAEItemStack> in = new ArrayList<>();
        final List<IAEItemStack> out = new ArrayList<>();

        for (int x = 0; x < inTag.tagCount(); x++) {
            NBTTagCompound ingredient = inTag.getCompoundTagAt(x);
            final ItemStack gs = stackFromNBT(ingredient);

            if (!ingredient.isEmpty() && gs.isEmpty()) {
                throw new IllegalArgumentException("Invalid pattern provided : " + is);
            }

            if (!gs.isEmpty()) {
                in.add(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(gs));
            }
        }

        for (int x = 0; x < outTag.tagCount(); x++) {
            NBTTagCompound resultItemTag = outTag.getCompoundTagAt(x);
            final ItemStack gs = stackFromNBT(resultItemTag);

            if (!resultItemTag.isEmpty() && gs.isEmpty()) {
                throw new IllegalArgumentException("Invalid pattern provided : " + is);
            }

            if (!gs.isEmpty()) {
                out.add(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(gs));
            }
        }

        IAEItemStack[] inputs = in.toArray(new IAEItemStack[0]);
        IAEItemStack[] outputs = out.toArray(new IAEItemStack[0]);


        final Map<IAEItemStack, IAEItemStack> tmpOutputs = new HashMap<>();

        for (final IAEItemStack io : outputs) {
            if (io == null) {
                continue;
            }

            final IAEItemStack g = tmpOutputs.get(io);

            if (g == null) {
                tmpOutputs.put(io, io.copy());
            } else {
                g.add(io);
            }
        }

        final Map<IAEItemStack, IAEItemStack> tmpInputs = new HashMap<>();

        for (final IAEItemStack io : inputs) {
            if (io == null) {
                continue;
            }

            final IAEItemStack g = tmpInputs.get(io);

            if (g == null) {
                tmpInputs.put(io, io.copy());
            } else {
                g.add(io);
            }
        }

        if (tmpOutputs.isEmpty() || tmpInputs.isEmpty()) {
            throw new IllegalArgumentException("Invalid pattern provided : " + is);
        }

        this.inputs = new IAEItemStack[tmpInputs.size()];
        int offset = 0;

        for (final IAEItemStack io : tmpInputs.values()) {
            this.inputs[offset] = io;
            offset++;
        }

        offset = 0;
        this.outputs = new IAEItemStack[tmpOutputs.size()];

        for (final IAEItemStack io : tmpOutputs.values()) {
            this.outputs[offset] = io;
            offset++;
        }

        this.patternItem = is;
        this.pattern = AEItemStack.fromItemStack(is);
    }

    @Override public abstract ItemStack getPattern();

    @Override
    public boolean isValidItemForSlot(int i, ItemStack itemStack, World world) {
        return true;
    }

    @Override
    public boolean isCraftable() {
        return true;
    }

    @Override
    public IAEItemStack[] getInputs() {
        return inputs;
    }

    @Override
    public IAEItemStack[] getCondensedInputs() {
        return inputs;
    }

    @Override
    public IAEItemStack[] getCondensedOutputs() {
        return outputs;
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return outputs;
    }

    @Override
    public IAEItemStack getPrimaryOutput() {
        return outputs[0];
    }

    @Override
    public boolean canSubstitute() {
        return false;
    }

    @Override
    public ItemStack getOutput(InventoryCrafting inventoryCrafting, World world) {
        return outputs[0].createItemStack();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int i) {
        priority = i;
    }
}
