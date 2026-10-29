package dev.beecube31.crazyae2.client.render;

import appeng.client.render.crafting.AbstractCraftingUnitModelProvider;
import appeng.client.render.crafting.LightBakedModel;
import appeng.core.AppEng;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;

public class CrazyCraftingBlocksModelProvider extends AbstractCraftingUnitModelProvider<CrazyAECraftingUnitType>
        implements ModelDebugName {
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

    public Material.Baked getLightMaterial(MaterialBaker textureGetter) {
        return switch (this.type) {
            case STORAGE_1MB -> textureGetter.get(STORAGE_1MB_LIGHT, this);
            case STORAGE_4MB -> textureGetter.get(STORAGE_4MB_LIGHT, this);
            case STORAGE_16MB -> textureGetter.get(STORAGE_16MB_LIGHT, this);
            case STORAGE_64MB -> textureGetter.get(STORAGE_64MB_LIGHT, this);
            case STORAGE_256MB -> textureGetter.get(STORAGE_256MB_LIGHT, this);
            case STORAGE_1GB -> textureGetter.get(STORAGE_1GB_LIGHT, this);
            case STORAGE_2GB -> textureGetter.get(STORAGE_2GB_LIGHT, this);
            case STORAGE_8GB -> textureGetter.get(STORAGE_8GB_LIGHT, this);
            case STORAGE_32GB -> textureGetter.get(STORAGE_32GB_LIGHT, this);
            case STORAGE_128GB -> textureGetter.get(STORAGE_128GB_LIGHT, this);

            case ACCELERATOR_4X -> textureGetter.get(ACCELERATOR_4X, this);
            case ACCELERATOR_16X -> textureGetter.get(ACCELERATOR_16X, this);
            case ACCELERATOR_64X -> textureGetter.get(ACCELERATOR_64X, this);
            case ACCELERATOR_256X -> textureGetter.get(ACCELERATOR_256X, this);
            case ACCELERATOR_1024X -> textureGetter.get(ACCELERATOR_1024X, this);
            case ACCELERATOR_4096X -> textureGetter.get(ACCELERATOR_4096X, this);
            case ACCELERATOR_16384X -> textureGetter.get(ACCELERATOR_16384X, this);
            case ACCELERATOR_65536X -> textureGetter.get(ACCELERATOR_65536X, this);
            case ACCELERATOR_262144X -> textureGetter.get(ACCELERATOR_262144X, this);
            case ACCELERATOR_1048576X -> textureGetter.get(ACCELERATOR_1048576X, this);
            case ACCELERATOR_4194304X -> textureGetter.get(ACCELERATOR_4194304X, this);
            case ACCELERATOR_CREATIVE -> textureGetter.get(ACCELERATOR_CREATIVE, this);
            case STORAGE_CREATIVE -> textureGetter.get(STORAGE_CREATIVE_LIGHT, this);
            case COMBINER -> textureGetter.get(LIGHT_BASE, this);
        };
    }

    @Override
    public BlockStateModel bake(MaterialBaker materialBaker) {
        Material.Baked ringCorner = materialBaker.get(RING_CORNER, this);
        Material.Baked ringSideHor = materialBaker.get(RING_SIDE_HOR, this);
        Material.Baked ringSideVer = materialBaker.get(RING_SIDE_VER, this);

        return new LightBakedModel(ringCorner, ringSideHor, ringSideVer, materialBaker.get(LIGHT_BASE, this),
                this.getLightMaterial(materialBaker));
    }

    @Override
    public String debugName() {
        return getClass().getName() + "[" + this.type + "]";
    }

    private static Material texture(String name) {
        return new Material(CrazyAE.makeId("block/crafting/" + name));
    }

    private static Material aeTexture(String name) {
        return new Material(AppEng.makeId("block/crafting/" + name));
    }
}
