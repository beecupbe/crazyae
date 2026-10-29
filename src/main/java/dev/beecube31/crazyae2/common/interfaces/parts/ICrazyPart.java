package dev.beecube31.crazyae2.common.interfaces.parts;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface ICrazyPart {
    boolean onCrazyPartActivate(Player player, InteractionHand hand, Vec3 pos);
}
