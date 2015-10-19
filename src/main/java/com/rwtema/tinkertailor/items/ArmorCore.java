package com.rwtema.tinkertailor.items;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.caches.Caches;
import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierInstance;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.nbt.ConfigKeys;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import com.rwtema.tinkertailor.render.ModelArmor;
import com.rwtema.tinkertailor.render.RendererHandler;
import com.rwtema.tinkertailor.render.font.CustomFontRenderer;
import com.rwtema.tinkertailor.render.textures.ArmorTextureManager;
import com.rwtema.tinkertailor.utils.ItemHelper;
import com.rwtema.tinkertailor.utils.Lang;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.modifier.IModifyable;

public class ArmorCore extends ItemArmor implements ISpecialArmor, IModifyable {

	public static final ArmorProperties ZERO_ARMORP_ROPERTIES = new ArmorProperties(0, 0, 0);
	public static final float[] ratings = new float[]{0.15F, 0.40F, 0.30F, 0.15F};

	public static final ArmorCore[] armors = new ArmorCore[4];
	public static final String[] traits = new String[0];
	public static final boolean weaponInvis = ConfigKeys.WeaponInvis.getBool(false);
	public static boolean DEBUG_ALWAYS_RELOAD = false;
	@SideOnly(Side.CLIENT)
	ModelArmor armor;

	public ArmorCore(int slot) {
		super(ArmorMaterial.IRON, 0, slot);
		setCreativeTab(TinkersTailor.creativeTabArmor);
		armors[slot] = this;
	}

	public static List<ModifierInstance> getModifiersIfUnbroken(@Nonnull ItemStack stack) {
		if (isBroken(stack))
			return ImmutableList.of();
		else
			return Caches.modifiers.get(stack);
	}

	public static boolean isBroken(@Nonnull ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return tag == null || tag.getCompoundTag(TinkersTailorConstants.NBT_MAINTAG).getBoolean(TinkersTailorConstants.NBT_MAINTAG_BROKEN);
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
		tag.setTag(TinkersTailorConstants.NBT_MAINTAG, infiTool);
		infiTool.setInteger(TinkersTailorConstants.NBT_MAINTAG_RENDERID, i);
		infiTool.setInteger(TinkersTailorConstants.NBT_MAINTAG_MATERIAL, i);
		infiTool.setInteger(TinkersTailorConstants.NBT_MAINTAG_MODIFIERS, 3);
		stack.setTagCompound(tag);
		stack.setStackDisplayName(defaultToolName(TConstructRegistry.toolMaterials.get(i)));
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
	public ModelArmor getModelArmor() {
		if (armor == null) armor = new ModelArmor(armorType);
		return armor;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		if (armor == null) armor = new ModelArmor(armorType);
		if (DEBUG_ALWAYS_RELOAD) {
			RendererHandler.instance.reloadRenderers();
			RendererHandler.rendererCustoms.clear();
			armor = new ModelArmor(armorSlot);
		}

		loadAnimation(entityLiving);
		loadArmorData(entityLiving, itemStack);

		return armor;
	}

	@SideOnly(Side.CLIENT)
	private void loadArmorData(EntityLivingBase entityLiving, ItemStack itemStack) {
		armor.setMaterial(Caches.material.get(itemStack));
		if (entityLiving.hurtTime > 0)
			armor.setInvisible(entityLiving, false);
		else {
			boolean invis = ModifierRegistry.invisibility.hasEffect(itemStack) && !entityLiving.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer) && (!ItemHelper.isPlayerHoldingWeapon() || !weaponInvis);
			armor.stepInvisible(entityLiving, invis);
		}
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
		return ZERO_ARMORP_ROPERTIES;
	}

	public float getDamageResistance(ItemStack armor) {
		if (isBroken(armor)) return 0;
		return Caches.damageResistance.get(armor);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		if (isBroken(armor))
			return 0;
		double a = Caches.damageResistance.get(armor).doubleValue() / 100.0 * 25;
		return (int) Math.round(a);
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {

	}

	public void damage(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage) {
		int originalDamage = damage;
		for (ModifierInstance modifierInstance : getModifiersIfUnbroken(stack)) {
			damage = modifierInstance.modifier.reduceArmorDamage(entity, stack, source, damage, originalDamage, armorType, modifierInstance.level);
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

		String ability = toolMaterial.ability();
		if (!ability.equals(""))
			toolTips.add(toolMaterial.style() + ability);

		List<ModifierInstance> modifiers = Caches.modifiers.get(stack);

		for (ModifierInstance modifier : modifiers) {
			modifier.modifier.addInfo(toolTips, player, stack, armorType, modifier.level);
		}

		toolTips.add("");
		toolTips.add(String.format(EnumChatFormatting.BLUE + "+%s%% " + Lang.translate("Damage Resistance") + EnumChatFormatting.RESET, String.format("%.1f", getDamageResistance(stack))));

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
		toolTips.add("----------------");
		toolTips.add(EnumChatFormatting.WHITE + Lang.translate("Full Armor Stats") + EnumChatFormatting.GRAY);

		float dR = 0;

		TreeSet<Modifier> modifierSet = new TreeSet<Modifier>();

		ItemStack[] armorInventory = player.inventory.armorInventory;
		for (int slot = 0; slot < armorInventory.length; slot++) {
			ItemStack itemStack = armorInventory[slot];
			if (itemStack == null) continue;

			Item itm = itemStack.getItem();
			if (itm instanceof ArmorCore) {
				ArmorCore item = (ArmorCore) itm;
				dR += item.getDamageResistance(itemStack) / 4;

				for (ModifierInstance modifier : Caches.modifiers.get(itemStack)) {
					modifierSet.add(modifier.modifier);
				}

			} else if (itm instanceof ISpecialArmor) {
				dR += ((ISpecialArmor) itm).getArmorDisplay(player, itemStack, slot) * 2 / 25.0;
			} else if (itm instanceof ItemArmor) {
				dR += ((ItemArmor) itm).getArmorMaterial().getDamageReductionAmount(slot) / 25.0;
			}
		}

		for (Modifier modifier : modifierSet) {
			modifier.addArmorSetInfo(toolTips, player);
		}
		toolTips.add(String.format("%s%% " + Lang.translate("Damage Resistance"), String.format("%.1f", dR * 4)));


	}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {
		return CustomFontRenderer.instance;
	}

	@Override
	public String getBaseTagName() {
		return TinkersTailorConstants.NBT_MAINTAG;
	}

	@Override
	public String getModifyType() {
		return TinkersTailorConstants.MODIFY_TYPE;
	}

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
		List<ModifierInstance> modifierInstances = Caches.tickers.get(itemStack);
		for (ModifierInstance modifierInstance : modifierInstances) {
			modifierInstance.modifier.onArmorTick(player, itemStack, armorType, modifierInstance.level);
		}
		super.onArmorTick(world, player, itemStack);
	}

	@Override
	public int getMaxDamage() {
		return 0;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return Caches.maxDurability.get(stack);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		if (damage != OreDictionary.WILDCARD_VALUE) {
			int maxDamage = getMaxDamage(stack);
			if (damage > maxDamage) {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag != null) {
					tag.getCompoundTag(TinkersTailorConstants.NBT_MAINTAG).setBoolean(TinkersTailorConstants.NBT_MAINTAG_BROKEN, true);
					damage = maxDamage;
				}
			}
		}
		super.setDamage(stack, damage);
	}
}
