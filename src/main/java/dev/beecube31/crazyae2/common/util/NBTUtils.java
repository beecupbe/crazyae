package dev.beecube31.crazyae2.common.util;

import appeng.api.storage.data.IAEItemStack;
import appeng.helpers.ItemStackHelper;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.common.tile.botania.TileBotaniaMechanicalMachineBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.*;

import static appeng.helpers.ItemStackHelper.stackWriteToNBT;

public class NBTUtils {

    public static NBTTagCompound openNbtDataNullable(final ItemStack i) {
        NBTTagCompound compound = i.getTagCompound();
        i.setTagCompound(compound);
        return compound;
    }

    public static NBTBase createItemTag(final ItemStack i) {
        final NBTTagCompound c = new NBTTagCompound();

        if (!i.isEmpty()) {
            stackWriteToNBT(i, c);
        }

        return c;
    }

    public static void writeQueueMapToNBT(List<? extends TileBotaniaMechanicalMachineBase.CraftingTask> queueMap, NBTTagCompound data, String tagName) {
        NBTTagList list = new NBTTagList();
        for (TileBotaniaMechanicalMachineBase.CraftingTask details : queueMap) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("progressAmt", details.getProgress());

            String taskType = getTaskType(details, tag);
            tag.setString("taskType", taskType);

            NBTTagList inputsTag = new NBTTagList();
            for (IAEItemStack input : details.getTaskItems()) {
                if (input != null) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    ItemStackHelper.stackWriteToNBT(input.asItemStackRepresentation(), itemTag);
                    inputsTag.appendTag(itemTag);
                }
            }
            tag.setTag("inputs", inputsTag);

            list.appendTag(tag);
        }
        data.setTag(tagName, list);
    }

    private static String getTaskType(TileBotaniaMechanicalMachineBase.CraftingTask details, NBTTagCompound nbt) {
        if (details instanceof TileBotaniaMechanicalMachineBase.ManaCraftingTask k) {
            nbt.setInteger("manaReq", k.getRequiredMana());
            return "manapool";
        } else if (details instanceof TileBotaniaMechanicalMachineBase.PureDaisyCraftingTask k) {
            nbt.setBoolean("reqBucket", k.requireOutputBucket());
            return "puredaisy";
        } else if (details instanceof TileBotaniaMechanicalMachineBase.RuneAltarCraftingTask k) {
            nbt.setInteger("manaReq", k.getRequiredMana());
            return "runealtar";
        } else if (details instanceof TileBotaniaMechanicalMachineBase.TeraplateCraftingTask k) {
            nbt.setInteger("manaReq", k.getRequiredMana());
            return "teraplate";
        } else if (details instanceof TileBotaniaMechanicalMachineBase.BreweryCraftingTask k) {
            nbt.setInteger("manaReq", k.getRequiredMana());
            return "brewery";
        } else if (details instanceof TileBotaniaMechanicalMachineBase.CraftingTask) {
            return "default";
        }

        throw new IllegalArgumentException("Invalid CraftingTask provided");
    }

    public static List<TileBotaniaMechanicalMachineBase.CraftingTask> readQueueMapFromNBT(NBTTagCompound data, String tagName) {
        List<TileBotaniaMechanicalMachineBase.CraftingTask> queueMap = new ArrayList<>();
        if (data.hasKey(tagName, Constants.NBT.TAG_LIST)) {
            NBTTagList list = data.getTagList(tagName, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int progressAmt = tag.getInteger("progressAmt");
                String taskType = tag.getString("taskType");

                NBTTagList inputsTag = tag.getTagList("inputs", Constants.NBT.TAG_COMPOUND);
                IAEItemStack[] inputs = new IAEItemStack[inputsTag.tagCount()];
                for (int j = 0; j < inputsTag.tagCount(); j++) {
                    ItemStack itemStack = ItemStackHelper.stackFromNBT(inputsTag.getCompoundTagAt(j));
                    inputs[j] = AEItemStack.fromItemStack(itemStack);
                }

                switch (taskType) {
                    case "manapool" -> {
                        TileBotaniaMechanicalMachineBase.CraftingTask task = new TileBotaniaMechanicalMachineBase.ManaCraftingTask(inputs, progressAmt, tag.getInteger("manaReq"));
                        queueMap.add(task);
                    }

                    case "puredaisy" -> {
                        TileBotaniaMechanicalMachineBase.CraftingTask task = new TileBotaniaMechanicalMachineBase.PureDaisyCraftingTask(inputs, progressAmt, tag.getBoolean("reqBucket"));
                        queueMap.add(task);
                    }

                    case "runealtar" -> {
                        TileBotaniaMechanicalMachineBase.CraftingTask task = new TileBotaniaMechanicalMachineBase.RuneAltarCraftingTask(inputs, progressAmt, tag.getInteger("manaReq"));
                        queueMap.add(task);
                    }

                    case "teraplate" -> {
                        TileBotaniaMechanicalMachineBase.CraftingTask task = new TileBotaniaMechanicalMachineBase.TeraplateCraftingTask(inputs, progressAmt, tag.getInteger("manaReq"));
                        queueMap.add(task);
                    }

                    case "brewery" -> {
                        TileBotaniaMechanicalMachineBase.CraftingTask task = new TileBotaniaMechanicalMachineBase.BreweryCraftingTask(inputs, progressAmt, tag.getInteger("manaReq"));
                        queueMap.add(task);
                    }

                    default -> {
                        TileBotaniaMechanicalMachineBase.CraftingTask task = new TileBotaniaMechanicalMachineBase.CraftingTask(inputs, progressAmt);
                        queueMap.add(task);
                    }
                }
            }
        }
        return queueMap;
    }


}
