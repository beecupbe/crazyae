
package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.*;
import appeng.api.implementations.IUpgradeableHost;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiCustomSlot;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.container.interfaces.IJEIGhostIngredients;
import appeng.container.slot.IJEITargetSlot;
import appeng.container.slot.SlotFake;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketConfigButton;
import appeng.core.sync.packets.PacketInventoryAction;
import appeng.fluids.client.gui.widgets.GuiFluidSlot;
import appeng.fluids.util.AEFluidStack;
import appeng.helpers.InventoryAction;
import appeng.parts.automation.PartExportBus;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.common.containers.ContainerCrazyAEUpgradeable;
import dev.beecube31.crazyae2.common.interfaces.mana.IManaLinkableDevice;
import dev.beecube31.crazyae2.common.parts.implementations.PartExportBusImp;
import dev.beecube31.crazyae2.common.parts.implementations.PartImportBusImp;
import dev.beecube31.crazyae2.common.parts.implementations.PartManaExportBus;
import dev.beecube31.crazyae2.common.parts.implementations.PartManaImportBus;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.networking.TilePatternsInterface;
import mezz.jei.api.gui.IGhostIngredientHandler.Target;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class GuiCrazyAEUpgradeable extends AEBaseGui implements IJEIGhostIngredients {
    private final Map<Target<?>, Object> mapTargetSlot = new HashMap<>();
    protected final ContainerCrazyAEUpgradeable cvb;
    protected final IUpgradeableHost bc;

    protected GuiImgButton redstoneMode;
    protected GuiImgButton fuzzyMode;
    protected GuiImgButton craftMode;
    protected GuiImgButton schedulingMode;

    public GuiCrazyAEUpgradeable(final InventoryPlayer inventoryPlayer, final IUpgradeableHost te) {
        this(new ContainerCrazyAEUpgradeable(inventoryPlayer, te));
    }

    public GuiCrazyAEUpgradeable(final ContainerCrazyAEUpgradeable te) {
        super(te);
        this.cvb = te;

        this.bc = (IUpgradeableHost) te.getTarget();
        this.xSize = this.hasToolbox() || this.drawPatternsInterfaceOutputSlots() ? 246 : 211;
        this.ySize = 184;
    }

    protected boolean hasToolbox() {
        return ((ContainerCrazyAEUpgradeable) this.inventorySlots).hasToolbox();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addButtons();
    }

    @Override
    public List<Rectangle> getJEIExclusionArea() {
        List<Rectangle> exclusionArea = new ArrayList<>();

        int yOffset = guiTop + 8;

        int visibleButtons = (int) this.buttonList.stream().filter(v -> v.enabled && v.x < guiLeft).count();
        Rectangle sortDir = new Rectangle(guiLeft - 18, yOffset, 18, visibleButtons * 18 + visibleButtons - 2);
        exclusionArea.add(sortDir);

        return exclusionArea;
    }

    protected void addButtons() {
        this.redstoneMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 8, Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.fuzzyMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 28, Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.craftMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 48, Settings.CRAFT_ONLY, YesNo.NO);
        this.schedulingMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 68, Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);

        this.buttonList.add(this.craftMode);
        this.buttonList.add(this.redstoneMode);
        this.buttonList.add(this.fuzzyMode);
        this.buttonList.add(this.schedulingMode);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        if (this.bc instanceof IManaLinkableDevice) {
            this.fontRenderer.drawString(CrazyAEGuiText.LINK_WITH_MANA_CONNECTOR.getLocal(), 8, 20, 4210752);
            this.fontRenderer.drawString(CrazyAEGuiText.LINK_WITH_MANA_CONNECTOR2.getLocal(), 8, 29, 4210752);
            this.fontRenderer.drawString(CrazyAEGuiText.LINK_WITH_MANA_CONNECTOR3.getLocal(), 8, 38, 4210752);
        }
        this.fontRenderer.drawString(this.getGuiDisplayName(this.getName().getLocal()), 8, 6, 4210752);
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752);

        if (this.redstoneMode != null) {
            this.redstoneMode.set(this.cvb.getRedStoneMode());
        }

        if (this.fuzzyMode != null) {
            this.fuzzyMode.set(this.cvb.getFuzzyMode());
        }

        if (this.craftMode != null) {
            this.craftMode.set(this.cvb.getCraftingMode());
        }

        if (this.schedulingMode != null) {
            this.schedulingMode.set(this.cvb.getSchedulingMode());
        }
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.handleButtonVisibility();

        if (!(this.bc instanceof IManaLinkableDevice)) {
            this.bindTexture(this.getBackground());
        } else {
            this.bindTexture(this.getManaBusBackground());
        }
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, 211 - 34, this.ySize);
        if (this.drawUpgrades()) {
            this.drawTexturedModalRect(offsetX + 177, offsetY, 177, 0, 35, 14 + this.cvb.availableUpgrades() * 18);
        }
        if (this.hasToolbox()) {
            this.drawTexturedModalRect(offsetX + 178, offsetY + this.ySize - 90, 178, this.ySize - 90, 68, 68);
        }
        if (this.drawPatternsInterfaceOutputSlots()) {
            this.drawTexturedModalRect(offsetX + 178, offsetY + this.ySize - 172, 178, this.ySize - 90, 68, 68);
        }
    }

    public void bindTexture(String file) {
        ResourceLocation loc = new ResourceLocation("crazyae", "textures/" + file);
        this.mc.getTextureManager().bindTexture(loc);
    }

    protected void handleButtonVisibility() {
        if (this.redstoneMode != null) {
            this.redstoneMode.setVisibility(this.bc.getInstalledUpgrades(Upgrades.REDSTONE) > 0);
        }
        if (this.fuzzyMode != null) {
            this.fuzzyMode.setVisibility(this.bc.getInstalledUpgrades(Upgrades.FUZZY) > 0);
        }
        if (this.craftMode != null) {
            this.craftMode.setVisibility(this.bc.getInstalledUpgrades(Upgrades.CRAFTING) > 0);
        }
        if (this.schedulingMode != null) {
            this.schedulingMode.setVisibility(this.bc.getInstalledUpgrades(Upgrades.CAPACITY) > 0 && this.bc instanceof PartExportBus);
        }
    }

    protected String getBackground() {
        return "guis/bus.png";
    }

    protected String getManaBusBackground() {
        return "guis/manabus.png";
    }

    protected boolean drawUpgrades() {
        return true;
    }

    protected boolean drawPatternsInterfaceOutputSlots() {
        return this.bc instanceof TilePatternsInterface;
    }

    protected CrazyAEGuiText getName() {
        return this.bc instanceof PartImportBusImp ? CrazyAEGuiText.IMP_IMPORT_BUS
                : this.bc instanceof PartExportBusImp ? CrazyAEGuiText.IMP_EXPORT_BUS
                : this.bc instanceof TilePatternsInterface ? CrazyAEGuiText.PATTERN_INTERFACE
                : this.bc instanceof PartManaImportBus ? CrazyAEGuiText.MANA_IMPORT_BUS
                : this.bc instanceof PartManaExportBus ? CrazyAEGuiText.MANA_EXPORT_BUS
                : CrazyAEGuiText.NOT_DEFINED;
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        final boolean backwards = Mouse.isButtonDown(1);

        if (btn == this.redstoneMode) {
            NetworkHandler.instance().sendToServer(new PacketConfigButton(this.redstoneMode.getSetting(), backwards));
        }

        if (btn == this.craftMode) {
            NetworkHandler.instance().sendToServer(new PacketConfigButton(this.craftMode.getSetting(), backwards));
        }

        if (btn == this.fuzzyMode) {
            NetworkHandler.instance().sendToServer(new PacketConfigButton(this.fuzzyMode.getSetting(), backwards));
        }

        if (btn == this.schedulingMode) {
            NetworkHandler.instance().sendToServer(new PacketConfigButton(this.schedulingMode.getSetting(), backwards));
        }
    }

    @Override
    public List<Target<?>> getPhantomTargets(Object ingredient) {
        mapTargetSlot.clear();

        FluidStack fluidStack = null;
        ItemStack itemStack = ItemStack.EMPTY;

        if (ingredient instanceof ItemStack) {
            itemStack = (ItemStack) ingredient;
            fluidStack = FluidUtil.getFluidContained(itemStack);
        } else if (ingredient instanceof FluidStack) {
            fluidStack = (FluidStack) ingredient;
        }

        if (!(ingredient instanceof ItemStack) && !(ingredient instanceof FluidStack)) {
            return Collections.emptyList();
        }

        List<Target<?>> targets = new ArrayList<>();

        List<IJEITargetSlot> slots = new ArrayList<>();
        if (!this.inventorySlots.inventorySlots.isEmpty()) {
            for (Slot slot : this.inventorySlots.inventorySlots) {
                if (slot instanceof SlotFake && !itemStack.isEmpty()) {
                    slots.add((IJEITargetSlot) slot);
                }
            }
        }
        if (!this.getGuiSlots().isEmpty()) {
            for (GuiCustomSlot slot : this.getGuiSlots()) {
                if (slot instanceof GuiFluidSlot && fluidStack != null) {
                    slots.add((IJEITargetSlot) slot);
                }
            }
        }
        for (Object slot : slots) {
            ItemStack finalItemStack = itemStack;
            FluidStack finalFluidStack = fluidStack;
            Target<Object> targetItem = new Target<Object>() {
                @Override
                public Rectangle getArea() {
                    if (slot instanceof SlotFake && ((SlotFake) slot).isSlotEnabled()) {
                        return new Rectangle(getGuiLeft() + ((SlotFake) slot).xPos, getGuiTop() + ((SlotFake) slot).yPos, 16, 16);
                    } else if (slot instanceof GuiFluidSlot && ((GuiFluidSlot) slot).isSlotEnabled()) {
                        return new Rectangle(getGuiLeft() + ((GuiFluidSlot) slot).xPos(), getGuiTop() + ((GuiFluidSlot) slot).yPos(), 16, 16);
                    }
                    return new Rectangle();
                }

                @Override
                public void accept(Object ingredient) {
                    PacketInventoryAction p = null;
                    try {
                        if (slot instanceof SlotFake && ((SlotFake) slot).isSlotEnabled()) {
                            if (finalItemStack.isEmpty() && finalFluidStack != null) {
                                p = new PacketInventoryAction(InventoryAction.PLACE_JEI_GHOST_ITEM, (IJEITargetSlot) slot, AEItemStack.fromItemStack(FluidUtil.getFilledBucket(finalFluidStack)));
                            } else if (!finalItemStack.isEmpty()) {
                                p = new PacketInventoryAction(InventoryAction.PLACE_JEI_GHOST_ITEM, (IJEITargetSlot) slot, AEItemStack.fromItemStack(finalItemStack));
                            }
                        } else {
                            if (finalFluidStack == null) {
                                return;
                            }
                            p = new PacketInventoryAction(InventoryAction.PLACE_JEI_GHOST_ITEM, (IJEITargetSlot) slot, AEItemStack.fromItemStack(AEFluidStack.fromFluidStack(finalFluidStack).asItemStackRepresentation()));
                        }
                        NetworkHandler.instance().sendToServer(p);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            targets.add(targetItem);
            mapTargetSlot.putIfAbsent(targetItem, slot);
        }
        return targets;
    }

    @Override
    public Map<Target<?>, Object> getFakeSlotTargetMap() {
        return mapTargetSlot;
    }
}
