package dev.beecube31.crazyae2.common.containers.base;

import appeng.api.parts.IPart;
import appeng.api.util.AEPartLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerOpenContext {

    private final boolean isItem;
    private World w;
    private int x;
    private int y;
    private int z;
    private AEPartLocation side;

    public ContainerOpenContext(final Object myItem) {
        final boolean isWorld = myItem instanceof IPart || myItem instanceof TileEntity;
        this.isItem = !isWorld;
    }

    public TileEntity getTile() {
        if (this.isItem) {
            return null;
        }
        return this.w.getTileEntity(new BlockPos(this.x, this.y, this.z));
    }

    public AEPartLocation getSide() {
        return this.side;
    }

    public void setSide(final AEPartLocation side) {
        this.side = side;
    }

    private int getZ() {
        return this.z;
    }

    public void setZ(final int z) {
        this.z = z;
    }

    private int getY() {
        return this.y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    private int getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    private World getWorld() {
        return this.w;
    }

    public void setWorld(final World w) {
        this.w = w;
    }
}
