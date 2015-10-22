package com.rwtema.tinkertailor.coremod;

import com.google.common.collect.Sets;
import java.util.HashSet;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import static org.objectweb.asm.Opcodes.F_SAME;
import static org.objectweb.asm.Opcodes.F_SAME1;
import static org.objectweb.asm.Opcodes.INTEGER;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class AltPotionTransformer implements IClassTransformer {
	HashSet<String> isPotionActiveField = Sets.newHashSet("func_70644_a", "func_82165_m", "isPotionActive");
	HashSet<String> getActivePotionEffectsFields = Sets.newHashSet("func_70651_bq", "getActivePotionEffect");
	String target = "net.minecraft.entity.EntityLivingBase";

	@Override
	public byte[] transform(String s, String s1, byte[] bytes) {
		if (!target.equals(s))
			return bytes;

		ClassNode classNode = new ClassNode(Opcodes.ASM5);
		ClassReader reader = new ClassReader(bytes);
		reader.accept(classNode, ClassReader.EXPAND_FRAMES);

		for (MethodNode m : classNode.methods) {
			if (isPotionActiveField.contains(m.name)) {
				AbstractInsnNode n = m.instructions.getFirst();
				LabelNode end = new LabelNode(), out = new LabelNode();
				m.instructions.insertBefore(n, n = new VarInsnNode(Opcodes.ALOAD, 0));

				if ("(Lnet/minecraft/potion/Potion;)Z".equals(m.desc)) {
					m.instructions.insert(n, n = new VarInsnNode(Opcodes.ALOAD, 1));
					String id = "isPotionActive".equals(m.name) ? "id" : "field_76415_H";
					m.instructions.insert(n, n = new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/Potion", id, "I"));
				} else
					m.instructions.insert(n, n = new VarInsnNode(Opcodes.ILOAD, 1));

				m.instructions.insert(n, n =
						new MethodInsnNode(Opcodes.INVOKESTATIC,
								"com/rwtema/tinkertailor/coremod/AltPotionHandler", "isPotionActive",
								"(Lnet/minecraft/entity/EntityLivingBase;I)I", false));

				m.instructions.insert(n, n = new InsnNode(Opcodes.DUP));
				m.instructions.insert(n, n = new JumpInsnNode(Opcodes.IFLT, end));
				m.instructions.insert(n, n = new JumpInsnNode(Opcodes.IFEQ, out));
				m.instructions.insert(n, n = new InsnNode(Opcodes.ICONST_1));
				m.instructions.insert(n, n = new InsnNode(Opcodes.IRETURN));
				m.instructions.insert(n, n = out);
				m.instructions.insert(n, n = new FrameNode(F_SAME, 0, null, 0, null));
				m.instructions.insert(n, n = new InsnNode(Opcodes.ICONST_0));
				m.instructions.insert(n, n = new InsnNode(Opcodes.IRETURN));
				m.instructions.insert(n, n = end);
				m.instructions.insert(n, n = new FrameNode(F_SAME1, 0, null, 0, new Object[] { INTEGER }));
				m.instructions.insert(n, n = new InsnNode(Opcodes.POP));
			}

			if (getActivePotionEffectsFields.contains(m.name)) {
				AbstractInsnNode n = m.instructions.getFirst();
				LabelNode end = new LabelNode();
				m.instructions.insertBefore(n, n = new VarInsnNode(Opcodes.ALOAD, 0));
				m.instructions.insert(n, n = new VarInsnNode(Opcodes.ALOAD, 1));

				m.instructions.insert(n, n =
						new MethodInsnNode(Opcodes.INVOKESTATIC,
								"com/rwtema/tinkertailor/coremod/AltPotionHandler", "getActivePotionEffect",
								"(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;)Lnet/minecraft/potion/PotionEffect;", false));

				m.instructions.insert(n, n = new InsnNode(Opcodes.DUP));
				m.instructions.insert(n, n = new JumpInsnNode(Opcodes.IFNULL, end));
				m.instructions.insert(n, n = new InsnNode(Opcodes.ARETURN));
				m.instructions.insert(n, n = end);
				m.instructions.insert(n, n = new FrameNode(F_SAME1, 0, null, 1, new Object[] { "net/minecraft/potion/PotionEffect" }));
				m.instructions.insert(n, n = new InsnNode(Opcodes.POP));
			}
		}


		ClassWriter writer = new ClassWriter(0);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
