
package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.*;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.container.slot.IJEITargetSlot;
import appeng.core.localization.GuiText;
import appeng.fluids.client.gui.widgets.GuiFluidSlot;
import appeng.fluids.util.AEFluidStack;
import appeng.helpers.InventoryAction;
import appeng.parts.automation.PartExportBus;
import appeng.util.BlockPosUtils;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.slot.AEFluidSlot;
import dev.beecube31.crazyae2.client.gui.slot.CustomSlot;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.StaticImageButton;
import dev.beecube31.crazyae2.common.containers.ContainerCrazyAEUpgradeable;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotFake;
import dev.beecube31.crazyae2.common.interfaces.jei.IJEIGhostIngredients;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.orig.PacketConfigButton;
import dev.beecube31.crazyae2.common.networking.packets.orig.PacketInventoryAction;
import dev.beecube31.crazyae2.common.parts.implementations.*;
import dev.beecube31.crazyae2.common.registration.upgrades.UpgradesInfoProvider;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiTooltip;
import dev.beecube31.crazyae2.common.tile.networking.TilePatternsInterface;
import dev.beecube31.crazyae2.common.tile.networking.TilePerfectInterface;
import dev.beecube31.crazyae2.common.util.BlockUtils;
import mezz.jei.api.gui.IGhostIngredientHandler.Target;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.lwjgl.input.Mouse;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.client.core.handler.HUDHandler;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static appeng.client.render.BlockPosHighlighter.hilightBlock;
import static dev.beecube31.crazyae2.core.client.CrazyAEClientHandler.ICON_INDENTATION;

public class GuiCrazyAEUpgradeable extends CrazyAEBaseGui implements IJEIGhostIngredients {
    private final Map<Target<?>, Object> mapTargetSlot = new HashMap<>();
    protected final ContainerCrazyAEUpgradeable cvb;
    protected final IUpgradesInfoProvider bc;

    protected GuiImgButton redstoneMode;
    protected GuiImgButton fuzzyMode;
    protected GuiImgButton craftMode;
    protected GuiImgButton schedulingMode;

    protected StaticImageButton manaDeviceHighlightBtn;

    protected boolean disableDrawTileName = false;
    protected boolean disableDrawInventoryString = false;

    protected Map<ItemStack, Integer> upgradesTooltipIcons = null;
    protected String upgradeTooltipStr = null;

    protected int myOffsetX = 0;

    public GuiCrazyAEUpgradeable(final InventoryPlayer inventoryPlayer, final IUpgradesInfoProvider te) {
        this(new ContainerCrazyAEUpgradeable(inventoryPlayer, te));
    }

    public GuiCrazyAEUpgradeable(final ContainerCrazyAEUpgradeable te) {
        super(te);
        this.cvb = te;

        this.bc = (IUpgradesInfoProvider) te.getTarget();
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

//        if (CrazyAEClientConfig.isAdvancedTooltipsEnabled()) {
//            this.addUpgradesTooltip();
//        }
    }

    protected void addUpgradesTooltip() {
        List<dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradesFeatureSetParser.FeatureEntry> entrys = UpgradesInfoProvider.getUpgradeInfo(this.cvb.getUpgradeable().getBlock());

        if (!entrys.isEmpty()) {
            StringBuilder str = new StringBuilder();
            this.upgradesTooltipIcons = new HashMap<>();
            str.append(CrazyAEGuiTooltip.THIS_DEVICE_SUPPORTS.getLocal());

            dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradesFeatureSetParser.FeatureEntry lastEntry = null;
            for (dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradesFeatureSetParser.FeatureEntry e : entrys) {
                if (lastEntry != null && lastEntry.upgradeType == e.upgradeType) continue;

                lastEntry = e;
                this.upgradesTooltipIcons.put(e.upgradeType.stack(1), this.upgradesTooltipIcons.size() + 1);
                str.append(ICON_INDENTATION).append(e.upgradesCount).append("x ").append(e.upgradeType.getLocalizedCardType()).append("\n");
            }

            this.upgradeTooltipStr = str.toString();
        }
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

        if (this.cvb.isThisManaDevice()) {
            this.buttonList.add((this.manaDeviceHighlightBtn = new StaticImageButton(
                    this.guiLeft + this.xSize - 56,
                    this.guiTop + 4,
                    StateSprite.HIGHLIGHT_BLOCK,
                    CrazyAEGuiText.HIGHLIGHT_MANA_BLOCK.getLocal(),
                    this.getGuiHue(),
                    777
            )));
            this.manaDeviceHighlightBtn.setDisableHue(true);
        }
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        if (this.cvb.isThisManaDevice()) {
            if (this.cvb.manaDeviceHasPool()) {
                this.drawString(
                        CrazyAEGuiText.LINKED_WITH_MANA_POOL.getLocal(),
                        25,
                        26
                );
                this.drawString(
                        String.format(
                                "x=%s y=%s z=%s",
                                this.cvb.getManaDevicePoolPosX(),
                                this.cvb.getManaDevicePoolPosY(),
                                this.cvb.getManaDevicePoolPosZ()
                        ),
                        25,
                        36
                );

                final BlockPos devicePos = new BlockPos(
                        this.cvb.getManaDevicePoolPosX(),
                        this.cvb.getManaDevicePoolPosY(),
                        this.cvb.getManaDevicePoolPosZ()
                );
                final ItemStack is = BlockUtils.getItemStackFromBlock(
                        this.bc.getTile().getWorld(),
                        devicePos
                );
                this.drawItem(
                        7,
                        24,
                        is
                );

                final TileEntity te = this.bc.getTile().getWorld().getTileEntity(devicePos);
                if (te instanceof IManaPool pool) {
                    this.drawString(
                            CrazyAEGuiText.MANA_AMT_TO_MAX.getLocal(),
                            25,
                            50
                    );

                    this.drawString(
                            String.format(
                                    "%s/%s",
                                    pool.getCurrentMana(),
                                    te.getUpdateTag().getInteger("manaCap")
                            ),
                            25,
                            60
                    );

                    GlStateManager.enableAlpha();
                    HUDHandler.renderManaBar(25, 70, 255, 1F, pool.getCurrentMana(), te.getUpdateTag().getInteger("manaCap"));
                    GlStateManager.disableAlpha();
                }
            } else {
                this.drawString(CrazyAEGuiText.LINK_WITH_MANA_CONNECTOR.getLocal(), 8, 20);
                this.drawString(CrazyAEGuiText.LINK_WITH_MANA_CONNECTOR2.getLocal(), 8, 29);
                this.drawString(CrazyAEGuiText.LINK_WITH_MANA_CONNECTOR3.getLocal(), 8, 38);
            }
        }

        if (!this.disableDrawTileName)
            this.drawString(this.getGuiDisplayName(this.getName().getLocal()), 8, 6);

        if (!this.disableDrawInventoryString)
            this.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3);

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

//        if (CrazyAEClientConfig.isAdvancedTooltipsEnabled() && this.upgradesTooltipIcons != null && this.upgradeTooltipStr != null) {
//            int[] guiSize = CrazyAEClientHandler.getCurrentGuiSize(this.mc);
//            int[] tooltipPos = CrazyAEClientHandler.getTooltipPos(
//                    Collections.singletonList(this.upgradeTooltipStr),
//                    mouseX,
//                    mouseY,
//                    guiSize[0],
//                    guiSize[1],
//                    -1,
//                    this.mc.fontRenderer
//            );
//
//            if (tooltipPos != null) {
//                int x = offsetX + (this.cvb.hasOptionSideButton() ? 223 : 187) + this.cvb.getMyOffsetX();
//                int y = offsetY + 8 + 18 * this.cvb.availableUpgrades();
//                if (x <= mouseX && x + 18 >= mouseX) {
//                    if (8 <= mouseY && 8 + y >= mouseY) {
//                        CrazyAEClientHandler.drawTooltip(
//                                ItemStack.EMPTY,
//                                Collections.singletonList(this.upgradeTooltipStr),
//                                mouseX,
//                                mouseY,
//                                guiSize[0],
//                                guiSize[1],
//                                -1,
//                                this.mc.fontRenderer
//                        );
//
//                        for (Map.Entry<ItemStack, Integer> e : upgradesTooltipIcons.entrySet()) {
//                            CrazyAEClientHandler.drawItemIntoTooltip(
//                                    e.getKey(),
//                                    e.getValue(),
//                                    mouseX,
//                                    mouseY
//                            );
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.handleButtonVisibility();

        if (this.cvb.isThisManaDevice()) {
            this.bindTexture(this.getManaBusBackground());
            if (this.cvb.manaDeviceHasPool()) {
                this.drawTexturedModalRect(
                        offsetX + 7,
                        offsetY + 24,
                        0,
                        184,
                        18,
                        18
                );
            }
        } else {
            this.bindTexture(this.getBackground());
        }

        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, 211 - 34, this.ySize);
        if (this.drawUpgrades()) {
            this.drawTexturedModalRect(
                    this.drawPatternsInterfaceOutputSlots() ? offsetX + 215 : offsetX + this.myOffsetX + 179,
                    offsetY,
                    this.drawPatternsInterfaceOutputSlots() ? 215 : 179,
                    0,
                    35,
                    14 + this.cvb.availableUpgrades() * 18
            );
        }
        if (this.hasToolbox()) {
            this.drawTexturedModalRect(
                    offsetX + 179,
                    offsetY + this.ySize - 90,
                    179,
                    this.ySize - 90,
                    68,
                    68
            );
        }
        if (this.drawPatternsInterfaceOutputSlots()) {
            this.drawTexturedModalRect(
                    offsetX + 179,
                    offsetY + 91,
                    179,
                    91,
                    68,
                    68
            );
        }
    }

    protected void setDisableDrawTileName(boolean v) {
        this.disableDrawTileName = v;
    }

    protected void setDisableDrawInventoryString(boolean v) {
        this.disableDrawInventoryString = v;
    }

    protected void handleButtonVisibility() {
        if (this.manaDeviceHighlightBtn != null) {
            this.manaDeviceHighlightBtn.setVisible(this.cvb.manaDeviceHasPool());
        }

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
        return this.bc instanceof TilePatternsInterface || this.bc instanceof PartPatternsInterface || this.bc instanceof TilePerfectInterface || this.bc instanceof PartPerfectInterface;
    }

    protected CrazyAEGuiText getName() {
        return this.bc instanceof PartImportBusImp ? CrazyAEGuiText.IMP_IMPORT_BUS
                : this.bc instanceof PartExportBusImp ? CrazyAEGuiText.IMP_EXPORT_BUS
                : this.bc instanceof TilePatternsInterface ? CrazyAEGuiText.PATTERN_INTERFACE
                : this.bc instanceof PartManaImportBus ? CrazyAEGuiText.MANA_IMPORT_BUS
                : this.bc instanceof PartManaExportBus ? CrazyAEGuiText.MANA_EXPORT_BUS
                : this.bc instanceof PartEnergyImportBus ? CrazyAEGuiText.ENERGY_IMPORT_BUS
                : this.bc instanceof PartEnergyExportBus ? CrazyAEGuiText.ENERGY_EXPORT_BUS
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

        if (btn == this.manaDeviceHighlightBtn && this.cvb.isThisManaDevice() && this.cvb.manaDeviceHasPool()) {
            final BlockPos poolPos = new BlockPos(
                    this.cvb.getManaDevicePoolPosX(),
                    this.cvb.getManaDevicePoolPosY(),
                    this.cvb.getManaDevicePoolPosZ()
            );
            final BlockPos bcPos = this.bc.getTile().getPos();
            final DimensionType bcDim = this.bc.getTile().getWorld().provider.getDimensionType();

            if (this.bc.getTile().getWorld().provider.getDimension()
                    != mc.player.getEntityWorld().provider.getDimension()
            ) {
                try {
                    mc.player.sendStatusMessage(
                            new TextComponentString(
                                    String.format(
                                            CrazyAEGuiText.MANA_BLOCK_HIGHLIGHTED_IN_ANOTHER_DIMENSION_WITH_POS.getLocal(),
                                            bcDim.getName(),
                                            bcDim.getId(),
                                            bcPos.getX(),
                                            bcPos.getY(),
                                            bcPos.getZ()
                                    )),
                            false
                    );
                } catch (Exception e) {
                    mc.player.sendStatusMessage(
                            new TextComponentString(CrazyAEGuiText.MANA_BLOCK_HIGHLIGHTED_IN_ANOTHER_DIMENSION.getLocal()),
                            false
                    );
                }
            } else {
                mc.player.sendStatusMessage(
                        new TextComponentString(
                                String.format(
                                        CrazyAEGuiText.MANA_BLOCK_HIGHLIGHTED_IN.getLocal(),
                                        bcPos.getX(),
                                        bcPos.getY(),
                                        bcPos.getZ()
                                )),
                        false
                );
                hilightBlock(
                        poolPos,
                        System.currentTimeMillis() + 500 * BlockPosUtils.getDistance(mc.player.getPosition(), poolPos),
                        bcDim.getId()
                );
            }

            mc.player.closeScreen();
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
            for (CustomSlot slot : this.getGuiSlots()) {
                if (slot instanceof AEFluidSlot && fluidStack != null) {
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
