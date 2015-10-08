package com.rwtema.tinkertailor.coremod;

import com.google.common.collect.Sets;
import java.util.ListIterator;
import java.util.Set;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import static org.objectweb.asm.Opcodes.ASM5;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PotionTransformer implements IClassTransformer {
	Set<String> updatePotionEffectsMethodNames = Sets.newHashSet("func_70679_bo", "updatePotionEffects");
	Set<String> spawnParticleMethodNames = Sets.newHashSet("func_72708_a", "spawnParticle");

	Set<String> activePotionsMapFieldNames = Sets.newHashSet("field_70713_bf", "activePotionsMap");
	String className = "net.minecraft.entity.EntityLivingBase";

	@Override
	public byte[] transform(String s, String s2, byte[] bytes) {
		if (!className.equals(s)) return bytes;

		ClassNode classNode = new ClassNode(ASM5);
		ClassReader reader = new ClassReader(bytes);
		reader.accept(classNode, ClassReader.EXPAND_FRAMES);

		FieldNode potionField = null;
		for (FieldNode field : classNode.fields) {
			if (activePotionsMapFieldNames.contains(field.name)) {
				potionField = field;
				break;
			}
		}

		if (potionField == null) return bytes;

		for (MethodNode method : classNode.methods) {
			if (updatePotionEffectsMethodNames.contains(method.name)) {
				InsnList insnList = method.instructions;
				ListIterator<AbstractInsnNode> iter = insnList.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode next = iter.next();
					if (next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode m = (MethodInsnNode) next;

						if (!spawnParticleMethodNames.contains(m.name))
							continue;

						insnList.insertBefore(m, new VarInsnNode(Opcodes.ALOAD, 0));
						insnList.insertBefore(m, new VarInsnNode(Opcodes.ALOAD, 0));
						insnList.insertBefore(m, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/EntityLivingBase", potionField.name, potionField.desc));

						m.name = "spawnParticle";
						m.owner = "com/rwtema/tinkertailor/coremod/PotionHandler";
						m.desc = "(Lnet/minecraft/world/World;Ljava/lang/String;DDDDDDLnet/minecraft/entity/EntityLivingBase;Ljava/util/HashMap;)V";
						m.itf = false;
						m.setOpcode(Opcodes.INVOKESTATIC);

						break;
					}
				}
				break;
			}
		}


		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
