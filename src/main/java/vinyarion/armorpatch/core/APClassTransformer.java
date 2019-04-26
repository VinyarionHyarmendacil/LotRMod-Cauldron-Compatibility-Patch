package vinyarion.armorpatch.core;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import lotr.compatibility.LOTRModChecker;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public class APClassTransformer implements IClassTransformer {

	public static Map<String, BiConsumer<ClassNode, Boolean>> classes = Maps.newHashMap();

	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		boolean isObfuscated = !name.equals(transformedName);
		if(classes.containsKey(transformedName)) {
			ClassNode classNode = new ClassNode();
	        ClassReader classReader = new ClassReader(basicClass);
	        classReader.accept(classNode, 0);
	        System.out.println("ArmorPatch: Transforming " + transformedName);
	        try {
	        	classes.get(transformedName).accept(classNode, isObfuscated);
	        	classes.remove(transformedName);
	        } catch(Exception e) {
	        	e.printStackTrace();
	        	return basicClass;
	        }
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);
            basicClass = classWriter.toByteArray();
		}
		return basicClass;
	}

	static {
		classes.put("net.minecraftforge.common.ISpecialArmor$ArmorProperties", (node, isObf) -> {
			for(MethodNode m : node.methods) {
				if($(m.name, "ApplyArmor") && $(m.desc, "(Lsv;[Ladd;Lro;DZ)F", "(Lnet/minecraft/entity/EntityLivingBase;[Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/DamageSource;DZ)F")) {
					FieldInsnNode nodeFound = null;
					for(AbstractInsnNode n : m.instructions.toArray()) {
						if(n instanceof FieldInsnNode) {
							FieldInsnNode fn = (FieldInsnNode)n;
							if($(fn.owner, "abb", "net/minecraft/item/ItemArmor") && $(fn.name, "c", "field_77879_b", "damageReduceAmount") && $(fn.desc, "I")) {
								m.instructions.insertBefore(fn, new VarInsnNode(Opcodes.ALOAD, /*7 is lotr, this will crash*/8));
								m.instructions.insertBefore(fn, new InsnNode(Opcodes.SWAP));
								m.instructions.insert(fn, new MethodInsnNode(Opcodes.INVOKESTATIC, "vinyarion/armorpatch/core/APHooks", "ap_hook_armor", isObf?"(Ladd;I)I":"(Lnet/minecraft/item/ItemStack;I)I", false));
								System.out.println("ArmorPatch: Finished custom without erroring.");
								System.out.println("ArmorPatch: Patched method " + m.name + " -- This is to fix the armor modifiers not working.");
								return;
							}
						}
					}
					System.err.println("ArmorPatch: Failed to patch method " + m.name);
				}
			}
			System.err.println("ArmorPatch: Failed to find the method! Is this even Cauldron?");
		});
		classes.put("net.minecraft.item.ItemStack", (node, isObf) -> {
//			boolean lotr = false;
			for(MethodNode m : node.methods) {
//				if(lotr) {
//					if($(m.name, "a", "func_96631_a", "attemptDamageItem") && $(m.desc, "(ILjava/util/Random;)Z")) {
//						m.instructions.clear();
//						InsnList newIns = new InsnList();
//						newIns.add(new VarInsnNode(Opcodes.ALOAD, 0));
//						newIns.add(new VarInsnNode(Opcodes.ILOAD, 1));
//						newIns.add(new VarInsnNode(Opcodes.ALOAD, 2));
//						newIns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "lotr/common/coremod/LOTRReplacedMethods$Enchants", "attemptDamageItem",
//								"(Lnet/minecraft/item/ItemStack;ILjava/util/Random;)Z", false));
//						newIns.add(new InsnNode(Opcodes.IRETURN));
//						m.instructions.insert(newIns);
//						System.out.println("ArmorPatch: Patched LOTR method " + m.name);
//						return;
//					}
//				} else {
					if($(m.name, "isDamaged") && $(m.desc, "(ILjava/util/Random;Lsv;)Z", "(ILjava/util/Random;Lnet/minecraft/entity/EntityLivingBase;)Z")) {
						for(AbstractInsnNode n : m.instructions.toArray()) {
							if(n.getOpcode() == Opcodes.ISUB) {
								InsnList insns = new InsnList();
								insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
								insns.add(new VarInsnNode(Opcodes.ILOAD, 1));
								insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
								insns.add(new VarInsnNode(Opcodes.ALOAD, 3));
								insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "vinyarion/armorpatch/core/APHooks", "ap_hook_itemstackdamage", isObf?"(ILadd;ILjava/util/Random;Lsv;)I":"(ILnet/minecraft/item/ItemStack;ILjava/util/Random;Lnet/minecraft/entity/EntityLivingBase;)I", false));
								m.instructions.insert(n, insns);
								System.out.println("ArmorPatch: Finished custom without erroring.");
								System.out.println("ArmorPatch: Patched LOTR method " + m.name + " -- This is to fix the item durability modifiers not working.");
								return;
							}
						}
						System.err.println("ArmorPatch: Failed to patch method " + m.name);
					}
//				}
			}
			System.err.println("ArmorPatch: Failed to find the method! Is this even Cauldron?");
		});
	}

	private static boolean $(String n, String... na) {
		for(String a : na) {
			if(a.equals(n)) {
				return true;
			}
		}
		return false;
	}
	
}
