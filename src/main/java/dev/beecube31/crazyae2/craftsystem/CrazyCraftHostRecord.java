package dev.beecube31.crazyae2.craftsystem;

import appeng.api.networking.crafting.ICraftingCPU;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;

import javax.annotation.Nonnull;

public class CrazyCraftHostRecord implements Comparable<CrazyCraftHostRecord> {

    private final String myName;
    private final ICrazyCraftHost cpu;
    private final ICraftingCPU aeCpu;
    private final long size;
    private final int processors;
    private final CraftingHostType type;

    public CrazyCraftHostRecord(final double size, final double coProcessors, final ICrazyCraftHost server, final CraftingHostType type) {
        this.size = (long) Math.min(size, Long.MAX_VALUE);
        this.processors = (int) Math.min(coProcessors, Integer.MAX_VALUE);
        this.cpu = server;
        this.aeCpu = null;
        this.myName = server.getName();
        this.type = type;
    }

    public CrazyCraftHostRecord(final double size, final double coProcessors, final ICraftingCPU server, final CraftingHostType type) {
        this.size = (long) Math.min(size, Long.MAX_VALUE);
        this.processors = (int) Math.min(coProcessors, Integer.MAX_VALUE);
        this.cpu = null;
        this.aeCpu = server;
        this.myName = server.getName();
        this.type = type;
    }

    @Override
    public int compareTo(@Nonnull final CrazyCraftHostRecord o) {
        final int a = Integer.compare(o.getProcessors(), this.getProcessors());
        if (a != 0) {
            return a;
        }
        return Long.compare(o.getSize(), this.getSize());
    }

    public CraftingHostType getType() {
        return this.type;
    }

    public ICrazyCraftHost getCrazyWorker() {
        return this.cpu;
    }

    public ICraftingCPU getCpu() {
        return this.aeCpu;
    }

    public String getName() {
        return this.myName;
    }

    public int getProcessors() {
        return this.processors;
    }

    public long getSize() {
        return this.size;
    }
}
