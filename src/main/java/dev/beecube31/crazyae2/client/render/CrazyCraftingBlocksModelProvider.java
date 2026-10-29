package dev.beecube31.crazyae2.client.render;

import appeng.client.render.crafting.AbstractCraftingUnitModelProvider;
import appeng.client.render.crafting.LightBakedModel;
import appeng.core.AppEng;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class CrazyCraftingBlocksModelProvider extends AbstractCraftingUnitModelProvider<CrazyAECraftingUnitType> {
    private static final Material ACCELERATOR_CREATIVE = texture("accelerator_creative_light");
    private static final Material STORAGE_CREATIVE_LIGHT = texture("crafting_storage_creative_light");
    private static final Material ACCELERATOR_4X = texture("accelerator_4x_light");
    private static final Material ACCELERATOR_16X = texture("accelerator_16x_light");
    private static final Material ACCELERATOR_64X = texture("accelerator_64x_light");
    private static final Material ACCELERATOR_256X = texture("accelerator_256x_light");
    private static final Material ACCELERATOR_1024X = texture("accelerator_1024x_light");
    private static final Material ACCELERATOR_4096X = texture("accelerator_4096x_light");
    private static final Material ACCELERATOR_16384X = texture("accelerator_16384x_light");
    private static final Material ACCELERATOR_65536X = texture("accelerator_65536x_light");
    private static final Material ACCELERATOR_262144X = texture("accelerator_262144x_light");
    private static final Material ACCELERATOR_1048576X = texture("accelerator_1048576x_light");
    private static final Material ACCELERATOR_4194304X = texture("accelerator_4194304x_light");
    private static final Material STORAGE_1MB_LIGHT = texture("crafting_storage_1mb_light");
    private static final Material STORAGE_4MB_LIGHT = texture("crafting_storage_4mb_light");
    private static final Material STORAGE_16MB_LIGHT = texture("crafting_storage_16mb_light");
    private static final Material STORAGE_64MB_LIGHT = texture("crafting_storage_64mb_light");
    private static final Material STORAGE_256MB_LIGHT = texture("crafting_storage_256mb_light");
    private static final Material STORAGE_1GB_LIGHT = texture("crafting_storage_1gb_light");
    private static final Material STORAGE_2GB_LIGHT = texture("crafting_storage_2gb_light");
    private static final Material STORAGE_8GB_LIGHT = texture("crafting_storage_8gb_light");
    private static final Material STORAGE_32GB_LIGHT = texture("crafting_storage_32gb_light");
    private static final Material STORAGE_128GB_LIGHT = texture("crafting_storage_128gb_light");
    private static final Material RING_CORNER = aeTexture("ring_corner");
    private static final Material RING_SIDE_HOR = aeTexture("ring_side_hor");
    private static final Material RING_SIDE_VER = aeTexture("ring_side_ver");
    private static final Material UNIT_BASE = aeTexture("unit_base");
    private static final Material LIGHT_BASE = aeTexture("light_base");
    private static final Material MONITOR_BASE = aeTexture("monitor_base");
    private static final Material MONITOR_LIGHT_DARK = aeTexture("monitor_light_dark");
    private static final Material MONITOR_LIGHT_MEDIUM = aeTexture("monitor_light_medium");
    private static final Material MONITOR_LIGHT_BRIGHT = aeTexture("monitor_light_bright");

    public CrazyCraftingBlocksModelProvider(CrazyAECraftingUnitType type) {
        super(type);
    }

    @Override
    public List<Material> getMaterials() {
        List<Material> materials = new ArrayList<>();
        materials.add(RING_CORNER);
        materials.add(RING_SIDE_HOR);
        materials.add(RING_SIDE_VER);
        materials.add(LIGHT_BASE);

        switch (this.type) {
            case STORAGE_1MB -> materials.add(STORAGE_1MB_LIGHT);
            case STORAGE_4MB -> materials.add(STORAGE_4MB_LIGHT);
            case STORAGE_16MB -> materials.add(STORAGE_16MB_LIGHT);
            case STORAGE_64MB -> materials.add(STORAGE_64MB_LIGHT);
            case STORAGE_256MB -> materials.add(STORAGE_256MB_LIGHT);
            case STORAGE_1GB -> materials.add(STORAGE_1GB_LIGHT);
            case STORAGE_2GB -> materials.add(STORAGE_2GB_LIGHT);
            case STORAGE_8GB -> materials.add(STORAGE_8GB_LIGHT);
            case STORAGE_32GB -> materials.add(STORAGE_32GB_LIGHT);
            case STORAGE_128GB -> materials.add(STORAGE_128GB_LIGHT);

            case ACCELERATOR_4X -> materials.add(ACCELERATOR_4X);
            case ACCELERATOR_16X -> materials.add(ACCELERATOR_16X);
            case ACCELERATOR_64X -> materials.add(ACCELERATOR_64X);
            case ACCELERATOR_256X -> materials.add(ACCELERATOR_256X);
            case ACCELERATOR_1024X -> materials.add(ACCELERATOR_1024X);
            case ACCELERATOR_4096X -> materials.add(ACCELERATOR_4096X);
            case ACCELERATOR_16384X -> materials.add(ACCELERATOR_16384X);
            case ACCELERATOR_65536X -> materials.add(ACCELERATOR_65536X);
            case ACCELERATOR_262144X -> materials.add(ACCELERATOR_262144X);
            case ACCELERATOR_1048576X -> materials.add(ACCELERATOR_1048576X);
            case ACCELERATOR_4194304X -> materials.add(ACCELERATOR_4194304X);
            case ACCELERATOR_CREATIVE -> materials.add(ACCELERATOR_CREATIVE);
            case STORAGE_CREATIVE -> materials.add(STORAGE_CREATIVE_LIGHT);
            default -> {}
        }

        return Collections.unmodifiableList(materials);
    }

    public TextureAtlasSprite getLightMaterial(Function<Material, TextureAtlasSprite> textureGetter) {
        return switch (this.type) {
            case STORAGE_1MB -> textureGetter.apply(STORAGE_1MB_LIGHT);
            case STORAGE_4MB -> textureGetter.apply(STORAGE_4MB_LIGHT);
            case STORAGE_16MB -> textureGetter.apply(STORAGE_16MB_LIGHT);
            case STORAGE_64MB -> textureGetter.apply(STORAGE_64MB_LIGHT);
            case STORAGE_256MB -> textureGetter.apply(STORAGE_256MB_LIGHT);
            case STORAGE_1GB -> textureGetter.apply(STORAGE_1GB_LIGHT);
            case STORAGE_2GB -> textureGetter.apply(STORAGE_2GB_LIGHT);
            case STORAGE_8GB -> textureGetter.apply(STORAGE_8GB_LIGHT);
            case STORAGE_32GB -> textureGetter.apply(STORAGE_32GB_LIGHT);
            case STORAGE_128GB -> textureGetter.apply(STORAGE_128GB_LIGHT);

            case ACCELERATOR_4X -> textureGetter.apply(ACCELERATOR_4X);
            case ACCELERATOR_16X -> textureGetter.apply(ACCELERATOR_16X);
            case ACCELERATOR_64X -> textureGetter.apply(ACCELERATOR_64X);
            case ACCELERATOR_256X -> textureGetter.apply(ACCELERATOR_256X);
            case ACCELERATOR_1024X -> textureGetter.apply(ACCELERATOR_1024X);
            case ACCELERATOR_4096X -> textureGetter.apply(ACCELERATOR_4096X);
            case ACCELERATOR_16384X -> textureGetter.apply(ACCELERATOR_16384X);
            case ACCELERATOR_65536X -> textureGetter.apply(ACCELERATOR_65536X);
            case ACCELERATOR_262144X -> textureGetter.apply(ACCELERATOR_262144X);
            case ACCELERATOR_1048576X -> textureGetter.apply(ACCELERATOR_1048576X);
            case ACCELERATOR_4194304X -> textureGetter.apply(ACCELERATOR_4194304X);
            case ACCELERATOR_CREATIVE -> textureGetter.apply(ACCELERATOR_CREATIVE);
            case STORAGE_CREATIVE -> textureGetter.apply(STORAGE_CREATIVE_LIGHT);
            case COMBINER -> textureGetter.apply(LIGHT_BASE);
        };
    }

    public BakedModel getBakedModel(Function<Material, TextureAtlasSprite> spriteGetter) {
        TextureAtlasSprite ringCorner = spriteGetter.apply(RING_CORNER);
        TextureAtlasSprite ringSideHor = spriteGetter.apply(RING_SIDE_HOR);
        TextureAtlasSprite ringSideVer = spriteGetter.apply(RING_SIDE_VER);

        return new LightBakedModel(ringCorner, ringSideHor, ringSideVer, spriteGetter.apply(LIGHT_BASE), this.getLightMaterial(spriteGetter));
    }

    private static Material texture(String name) {
        return new Material(InventoryMenu.BLOCK_ATLAS, CrazyAE.makeId("block/crafting/" + name));
    }

    private static Material aeTexture(String name) {
        return new Material(InventoryMenu.BLOCK_ATLAS, AppEng.makeId("block/crafting/" + name));
    }
}
