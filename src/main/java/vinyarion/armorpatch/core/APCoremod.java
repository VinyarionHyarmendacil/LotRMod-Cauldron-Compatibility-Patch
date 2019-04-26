package vinyarion.armorpatch.core;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

import java.util.Map;

@MCVersion("1.7.10")
@TransformerExclusions({"vinyarion.armorpatch.core."})
public class APCoremod implements IFMLLoadingPlugin {

	static {
		System.out.println("ArmorPatch: Found APCoremod!");
	}

	public String[] getASMTransformerClass() {
		return new String[]{"vinyarion.armorpatch.core.APClassTransformer"};
	}

	public String getSetupClass() {
		return null;
	}

	public void injectData(Map<String, Object> data) {
	}

	public String getModContainerClass() {
		return null;
	}

	public String getAccessTransformerClass() {
		return null;
	}

}