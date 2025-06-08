package dev.beecube31.crazyae2.common.registration.definitions;

import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.Tags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CreativeTab extends CreativeTabs {
	public static final CreativeTabs instance = new CreativeTab();

	public CreativeTab() {
		super(Tags.MODID);
	}

	@Override
	public @NotNull ItemStack getIcon() {
		return CrazyAE.icon();
	}

	@Override
	public ItemStack createIcon() {
		return CrazyAE.icon();
	}
}
