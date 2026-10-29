package dev.beecube31.crazyae2.common.enums;

import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public enum DriveSlotState {
    EMPTY("drive_cell_empty"), OFFLINE("drive_cell_off"), ONLINE("drive_cell_on"),
    TYPES_FULL("drive_cell_types_full"), FULL("drive_cell_full"), NO_CONTENTS("drive_cell_no_contents");

    public static final ModelProperty<DriveSlotState[]> SLOTS = new ModelProperty<>();

    public final ResourceLocation model;

    DriveSlotState(String name) {
        this.model = CrazyAE.makeId("block/drive_cells/" + name);
    }
}
