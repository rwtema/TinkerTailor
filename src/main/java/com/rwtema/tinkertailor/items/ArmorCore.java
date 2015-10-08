package com.rwtema.tinkertailor.items;

import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.DamageEventHandler;
import com.rwtema.tinkertailor.TinkerTailor;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.caches.Caches;
import com.rwtema.tinkertailor.nbt.TinkerTailorConstants;
import com.rwtema.tinkertailor.render.ModelArmor;
import com.rwtema.tinkertailor.render.RendererHandler;
import com.rwtema.tinkertailor.render.font.FontRendererShields;
import com.rwtema.tinkertailor.render.textures.ArmorTextureManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.modifier.IModifyable;

public class ArmorCore extends ItemArmor implements ISpecialArmor, IModifyable {

	public static final ArmorProperties broken = new ArmorProperties(0, 0, 0);
	public static final float[] ratings = new float[]{0.15F, 0.40F, 0.30F, 0.15F};

	public static final ArmorCore[] armors = new ArmorCore[4];

	public ArmorCore(int slot) {
		super(ArmorMaterial.IRON, 0, slot);
		setCreativeTab(TinkerTailor.creativeTabArmor);
		armors[slot] = this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (stack.hasTagCompound()) {
			return ArmorTextureManager.getManager(slot).getArmorName(Caches.material.get(stack));
		}

		return "GargleBargle:Nonsense";
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (Map.Entry<Integer, tconstruct.library.tools.ToolMaterial> entry : TConstructRegistry.toolMaterials.entrySet()) {
			int i = entry.getKey();
			ItemStack stack = createDefaultStack(i);
			list.add(stack);
		}
	}

	public ItemStack createDefaultStack(int i) {
		ItemStack stack = new ItemStack(this);
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound infiTool = new NBTTagCompound();
		tag.setTag(TinkerTailorConstants.NBT_MAINTAG, infiTool);
		infiTool.setInteger(TinkerTailorConstants.NBT_MAINTAG_RENDERID, i);
		infiTool.setInteger(TinkerTailorConstants.NBT_MAINTAG_MATERIAL, i);
		infiTool.setInteger("Modifiers", 3);
		stack.setTagCompound(tag);
		stack.setStackDisplayName("\u00A7f" + defaultToolName(TConstructRegistry.toolMaterials.get(i)));
		return stack;
	}

	private String defaultToolName(tconstruct.library.tools.ToolMaterial material) {
		return String.format("%s %s", material.prefixName(), StatCollector.translateToLocal(getUnlocalizedName() + ".name"));
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int j) {
		return Caches.color.get(stack);
	}

	@SideOnly(Side.CLIENT)
	ModelArmor armor;

	public static final boolean DEBUG_ALWAYS_RELOAD = false;

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		if (armor == null) armor = new ModelArmor(armorSlot);
		if (DEBUG_ALWAYS_RELOAD) {
			RendererHandler.instance.reloadRenderers();
			RendererHandler.rendererCustoms.clear();
			armor = new ModelArmor(armorSlot);
		}

		loadAnimation(entityLiving);
		armor.setMaterial(Caches.material.get(itemStack));
		return armor;
	}

	@SideOnly(Side.CLIENT)
	private void loadAnimation(EntityLivingBase entityLiving) {
		ItemStack heldItem = entityLiving.getHeldItem();
		armor.heldItemRight = heldItem != null ? 1 : 0;
		armor.aimedBow = false;

		if (heldItem != null && entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).getItemInUseCount() > 0) {
			EnumAction action = heldItem.getItemUseAction();
			if (action == EnumAction.block) {
				armor.heldItemRight = 3;
			} else if (action == EnumAction.bow) {
				armor.aimedBow = true;
			}
		}
		armor.isSneak = entityLiving.isSneaking();
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		return broken;
	}

	public float getDamageResistance(ItemStack armor) {
		return Caches.damageResistance.get(armor);
	}

	public float getDamageThreshold(ItemStack armor) {
		return Caches.damageThreshold.get(armor);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		if (!armor.hasTagCompound())
			return 0;
		int i = Caches.material.get(armor);
		return (int) (DamageEventHandler.matDRcache.get(i) * ratings[armorType] + 0.5F);
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		int originalDamage = damage;
		for (ModifierRegistry.ArmorModifierInstance armorModifierInstance : Caches.modifiers.get(stack)) {
			damage = armorModifierInstance.modifier.reduceArmorDamage(entity, stack, source, damage, originalDamage, armorType, armorModifierInstance.level);
		}

		stack.damageItem(damage, entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List toolTips, boolean debug) {
		super.addInformation(stack, player, toolTips, debug);
		if (stack == null) return;

		int i = Caches.material.get(stack);
		tconstruct.library.tools.ToolMaterial toolMaterial = TConstructRegistry.toolMaterials.get(i);
		if (toolMaterial == null) return;

		toolTips.add(StatCollector.translateToLocalFormatted("tooltip.resistance", String.format("%.2f%s", getDamageResistance(stack) / 2, FontRendererShields.shield_char)));
		toolTips.add(StatCollector.translateToLocalFormatted("tooltip.threshold", String.format("%.1f", getDamageThreshold(stack))));

		List<ModifierRegistry.ArmorModifierInstance> modifiers = Caches.modifiers.get(stack);
		for (ModifierRegistry.ArmorModifierInstance modifier : modifiers) {
			modifier.modifier.addInfo(toolTips, player, stack, armorType, modifier.level);
		}

		if (player == null) return;
		boolean found = false;
		for (ItemStack itemStack : player.inventory.armorInventory) {
			if (itemStack == stack) {
				found = true;
				break;
			}
		}
		if (!found) return;


		toolTips.add("");
		toolTips.add(EnumChatFormatting.WHITE + "Full Armor Stats" + EnumChatFormatting.GRAY);

		float dR = 0, dT = 0;

		HashSet<Modifier> modifierSet = new HashSet<Modifier>();
		ItemStack[] armorInventory = player.inventory.armorInventory;
		for (int slot = 0; slot < armorInventory.length; slot++) {
			ItemStack itemStack = armorInventory[slot];
			if (itemStack == null) continue;

			Item itm = itemStack.getItem();
			if (itm instanceof ArmorCore) {
				ArmorCore item = (ArmorCore) itm;
				dR += item.getDamageResistance(itemStack);
				dT += item.getDamageThreshold(itemStack);

				for (ModifierRegistry.ArmorModifierInstance modifier : Caches.modifiers.get(itemStack)) {
					modifierSet.add(modifier.modifier);
				}

			} else if (itm instanceof ISpecialArmor) {
				dR += ((ISpecialArmor) itm).getArmorDisplay(player, itemStack, slot) * 2 / 25.0;
			} else if (itm instanceof ItemArmor) {
				dR += ((ItemArmor) itm).getArmorMaterial().getDamageReductionAmount(slot) / 25.0;
			}
		}

		toolTips.add(String.format("Damage Resistance: %s", String.format("%.2f%s", dR / 2, FontRendererShields.shield_char)));
		toolTips.add(String.format("Damage Threshold: %s", String.format("%.1f", dT)));

		for (Modifier modifier : modifierSet) {
			modifier.addArmorSetInfo(toolTips, player);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {
		return FontRendererShields.instance;
	}

	@Override
	public String getBaseTagName() {
		return TinkerTailorConstants.NBT_MAINTAG;
	}

	@Override
	public String getModifyType() {
		return TinkerTailorConstants.MODIFY_TYPE;
	}

	public static final String[] traits = new String[0];

	@Override
	public String[] getTraits() {
		return traits;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack stack) {
		return Caches.attributes.get(stack);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		List<ModifierRegistry.ArmorModifierInstance> armorModifierInstances = Caches.tickers.get(itemStack);
		for (ModifierRegistry.ArmorModifierInstance armorModifierInstance : armorModifierInstances) {
			armorModifierInstance.modifier.onArmorTick(player, itemStack, armorType, armorModifierInstance.level);
		}
		super.onArmorTick(world, player, itemStack);
	}

	public boolean isBroken(ItemStack stack){
		return false;
	}
}
