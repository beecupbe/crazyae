package dev.beecube31.crazyae2.integrations.ct;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;

import java.util.ArrayList;
import java.util.List;

public class CraftTweakerPlugin {
	public static final List<IAction> ACTIONS = new ArrayList<>();

	public static void postInit() {
		try {
			CraftTweakerPlugin.ACTIONS.forEach(CraftTweakerAPI::apply);
		} catch(RuntimeException e) {
			CraftTweakerAPI.getLogger().logError("[CrazyAE] There were some problems applying an action", e);
		}
	}
}
