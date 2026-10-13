package dev.beecube31.crazyae2.mixins.parts;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickingRequest;
import appeng.me.service.TickManagerService;
import dev.beecube31.crazyae2.common.interfaces.parts.ICrazyPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TickManagerService.class, remap = false)
public class MixinTickManagerService {
    @Redirect(method = "addNode", at = @At(value = "INVOKE",
            target = "Lappeng/api/networking/ticking/IGridTickable;getTickingRequest(Lappeng/api/networking/IGridNode;)Lappeng/api/networking/ticking/TickingRequest;"))
    private TickingRequest getTickingRequest(IGridTickable instance, IGridNode iGridNode) {
        if (instance instanceof ICrazyPart cp) {
            return cp.getCrazyTickingRequest(iGridNode);
        }

        return instance.getTickingRequest(iGridNode);
    }
}
