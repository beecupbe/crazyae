package dev.beecube31.crazyae2.common.creativetab;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.ItemDefinition;
import appeng.core.localization.GuiText;
import appeng.items.AEBaseItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.ArrayList;
import java.util.List;

public class CrazyAETab {
    private static final List<ItemDefinition<?>> itemDefs = new ArrayList<>();

    public static void init(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .title(GuiText.CreativeTab.text())
                .icon(() -> AEBlocks.CONTROLLER.stack(1))
                .displayItems(CrazyAETab::buildDisplayItems)
                .build();
        Registry.register(registry, ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("crazyae", "main")), tab);
    }

    public static void add(ItemDefinition<?> itemDef) {
        itemDefs.add(itemDef);
    }

    private static void buildDisplayItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        for (var itemDef : itemDefs) {
            var item = itemDef.asItem();

            if (item instanceof AEBaseBlockItem baseItem
                    && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(itemDisplayParameters, output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(itemDisplayParameters, output);
            } else {
                output.accept(itemDef);
            }
        }
    }
}
