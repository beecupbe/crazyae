package dev.beecube31.crazyae2.common.items;

import appeng.api.implementations.items.IItemGroup;
import appeng.api.implementations.tiles.ISegmentedInventory;
import appeng.api.parts.IPartHost;
import appeng.core.features.IStackSrc;
import appeng.items.AEBaseItem;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.inv.AdaptorItemHandler;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEUpgradeHost;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEUpgradeModule;
import dev.beecube31.crazyae2.common.registration.definitions.Upgrades;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrazyAEUpgradeModule extends AEBaseItem implements ICrazyAEUpgradeModule {
	public static CrazyAEUpgradeModule instance;
	private final Int2ObjectOpenHashMap<Upgrades.UpgradeType> dmgToUpgrade = new Int2ObjectOpenHashMap<>();

	public CrazyAEUpgradeModule() {
		this.setHasSubtypes(true);

		instance = this;
	}

	@Override
	public @NotNull String getTranslationKey(ItemStack itemStack) {
		var upgrade = CrazyAE.definitions().upgrades().getById(itemStack.getItemDamage());
		if (upgrade.isPresent()) {
			return upgrade.get().getTranslationKey();
		}

		return "item.crazyae.invalid";
	}

	@Override
	protected void getCheckedSubItems(final CreativeTabs creativeTab, final NonNullList<ItemStack> itemStacks) {
		if (!this.isInCreativeTab(creativeTab)) return;

		for (var upgrade : Upgrades.UpgradeType.getCachedValues().values()) {
			if (!upgrade.isRegistered()) continue;

			itemStacks.add(new ItemStack(this, 1, upgrade.ordinal()));
		}
	}

	@SideOnly(Side.CLIENT)
	public void addCheckedInformation(ItemStack stack, World world, List<String> lines,
	                                  ITooltipFlag advancedTooltips) {
		Upgrades.UpgradeType u = this.getType(stack);
		if (u != null) {
			List<String> textList = new ArrayList<>();
			u.addCheckedInformation(stack, world, lines, advancedTooltips);
			if (!lines.isEmpty()) {
				lines.add("");
			}

			for (Map.Entry<ItemStack, Integer> j : u.getSupported().entrySet()) {
				String name = null;
				int limit = j.getValue();
				if (j.getKey().getItem() instanceof IItemGroup ig) {
					String str = ig.getUnlocalizedGroupName(u.getSupported().keySet(), j.getKey());
					if (str != null) {
						name = Platform.gui_localize(str) + (limit > 1 ? " (" + limit + ')' : "");
					}
				}

				if (name == null) {
					name = j.getKey().getDisplayName() + (limit > 1 ? " (" + limit + ')' : "");
				}

				if (!textList.contains(name)) {
					textList.add(name);
				}
			}

			Collator locale = Collator.getInstance(
					Minecraft
						.getMinecraft()
						.getLanguageManager()
						.getCurrentLanguage()
						.getJavaLocale()
			);

			if (locale != null) {
				textList.sort(locale);
			}

			lines.addAll(textList);
		}
	}

	public @Nullable Upgrades.UpgradeType getType(ItemStack stack) {
		return CrazyAE.definitions().upgrades().getById(stack.getItemDamage()).orElse(null);
	}

	@Override
	public @NotNull EnumActionResult onItemUseFirst(EntityPlayer player, @NotNull World world, @NotNull BlockPos pos,
	                                                @NotNull EnumFacing side, float hitX, float hitY, float hitZ,
	                                                @NotNull EnumHand hand) {
		if (player.isSneaking()) {
			var te = world.getTileEntity(pos);
			IItemHandler upgrades = null;
			if (te instanceof IPartHost) {
				var sp = ((IPartHost) te).selectPart(new Vec3d(hitX, hitY, hitZ));
				if (sp.part instanceof ICrazyAEUpgradeHost) {
					upgrades = ((ISegmentedInventory) sp.part).getInventoryByName("upgrades");
				}
			} else if (te instanceof ICrazyAEUpgradeHost) {
				upgrades = ((ISegmentedInventory) te).getInventoryByName("upgrades");
			}

			if (upgrades != null && !player.getHeldItem(hand).isEmpty() && player.getHeldItem(hand).getItem() instanceof ICrazyAEUpgradeModule um) {
				var u = um.getType(player.getHeldItem(hand));
				if (u != null) {
					if (player.world.isRemote) {
						return EnumActionResult.PASS;
					}

					InventoryAdaptor ad = new AdaptorItemHandler(upgrades);
					player.setHeldItem(hand, ad.addItems(player.getHeldItem(hand)));
					return EnumActionResult.SUCCESS;
				}
			}
		}

		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	public IStackSrc createUpgrade(Upgrades.UpgradeType upgradeType) {
		Preconditions.checkState(!upgradeType.isRegistered(), "Cannot create the same material twice.");

		var enabled = upgradeType.isEnabled();

		upgradeType.setStackSrc(new Upgrades.UpgradeStackSrc(upgradeType,
			enabled));

		if (enabled) {
			upgradeType.setItemInstance(this);
			upgradeType.markReady();
			final var newUpgradeNum = upgradeType.getDamageValue();

			if (this.dmgToUpgrade.get(newUpgradeNum) == null) {
				this.dmgToUpgrade.put(newUpgradeNum, upgradeType);
			} else {
				throw new IllegalStateException("Meta Overlap detected.");
			}
		}

		return upgradeType.getStackSrc();
	}
}
