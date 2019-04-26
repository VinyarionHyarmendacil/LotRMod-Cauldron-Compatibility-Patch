package vinyarion.armorpatch.core;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import lotr.common.coremod.LOTRReplacedMethods;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.enchant.LOTREnchantmentHelper;
import lotr.common.enchant.LOTREnchantmentProtection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.common.util.Constants.NBT;

public class APHooks {

	public static int ap_hook_armor(ItemStack stack, int unmodified) {
		return LOTRReplacedMethods.Enchants.getDamageReduceAmount(stack);
	}

	public static int ap_hook_itemstackdamage(int unmodified, ItemStack stack, int damages, Random random, EntityLivingBase elb) {
		int ret = unmodified;
		for(int i = 0; i < damages; i++) {
			if(LOTREnchantmentHelper.negateDamage(stack, random)) {
				--ret;
			}
		}
		return ret;
	}

}
