package dev.beecube31.crazyae2.client.rendering;

import appeng.bootstrap.BlockRenderingCustomizer;
import appeng.bootstrap.IBlockRendering;
import appeng.bootstrap.IItemRendering;
import dev.beecube31.crazyae2.client.rendering.models.ImprovedDriveModel;

public class ImprovedDriveRendering extends BlockRenderingCustomizer {
    @Override
    public void customize(IBlockRendering rendering, IItemRendering itemRendering) {
        rendering.builtInModel("models/block/builtin/driveimp", new ImprovedDriveModel());
    }
}
