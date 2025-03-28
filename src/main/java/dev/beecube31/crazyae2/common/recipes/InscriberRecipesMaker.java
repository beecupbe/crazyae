package dev.beecube31.crazyae2.common.recipes;

import appeng.api.AEApi;
import appeng.api.features.IInscriberRecipeBuilder;
import appeng.api.features.IInscriberRegistry;
import appeng.api.features.InscriberProcessType;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class InscriberRecipesMaker {

    private static final IInscriberRecipeBuilder builder = AEApi.instance().registries().inscriber().builder();
    private static final IInscriberRegistry reg = AEApi.instance().registries().inscriber();

    public static void make(@Nonnull ItemStack in, @Nonnull ItemStack top, @Nonnull ItemStack bottom, @Nonnull ItemStack output, @Nonnull InscriberProcessType type) {
        Set<ItemStack> inStack = new HashSet<>();
        Set<ItemStack> inStackTop = new HashSet<>();
        Set<ItemStack> inStackBottom = new HashSet<>();

        inStack.add(in);
        inStackTop.add(top);
        inStackBottom.add(bottom);
        reg.addRecipe(
                builder.withProcessType(type)
                        .withOutput(output)
                        .withInputs(inStack)
                        .withTopOptional(inStackTop)
                        .withBottomOptional(inStackBottom)
                        .build()
        );

    }

    public static void init() {
        CrazyAE.definitions().materials().quantumProcessor().maybeStack(1).ifPresent(quantumProcessor -> {
            make(
                    AEApi.instance().definitions().materials().matterBall().maybeStack(1).orElse(ItemStack.EMPTY),
                    AEApi.instance().definitions().materials().logicProcessor().maybeStack(1).orElse(ItemStack.EMPTY),
                    AEApi.instance().definitions().materials().engProcessor().maybeStack(1).orElse(ItemStack.EMPTY),
                    quantumProcessor,
                    InscriberProcessType.PRESS
            );

            CrazyAE.definitions().materials().energyProcessor().maybeStack(1).ifPresent(energyProcessor -> {
                make(
                    AEApi.instance().definitions().materials().matterBall().maybeStack(1).orElse(ItemStack.EMPTY),
                    CrazyAE.definitions().materials().quantumProcessor().maybeStack(1).orElse(ItemStack.EMPTY),
                    CrazyAE.definitions().blocks().fluixilizedBlock().maybeStack(1).orElse(ItemStack.EMPTY),
                    energyProcessor,
                    InscriberProcessType.PRESS
                );
            });
        });
    }
}
