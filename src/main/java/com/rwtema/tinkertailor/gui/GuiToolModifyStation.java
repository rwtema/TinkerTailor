package com.rwtema.tinkertailor.gui;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import com.rwtema.tinkertailor.blocks.TileEntityToolModifyStation;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import tconstruct.tools.gui.ToolStationGuiHelper;
import tconstruct.tools.inventory.ToolStationContainer;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class GuiToolModifyStation extends GuiContainer implements INEIGuiHandler {
	public TileEntityToolModifyStation logic;
	public ToolStationContainer toolSlots;


	public int guiType;
	public int[] slotX, slotY, iconX, iconY;

	public String title, body = "";

	public GuiToolModifyStation(InventoryPlayer inventoryplayer, TileEntityToolModifyStation stationlogic, World world, int x, int y, int z) {
		super(stationlogic.getGuiContainer(inventoryplayer, world, x, y, z));
		this.logic = stationlogic;
		toolSlots = (ToolStationContainer) inventorySlots;

		resetGui();
	}

	void resetGui() {
		guiType = 0;
		setSlotType(0);
		iconX = new int[]{0, 1, 2};
		iconY = new int[]{13, 13, 13};
		title = "\u00A7n" + StatCollector.translateToLocal("gui.toolmodifystation1");
		body = StatCollector.translateToLocal("gui.toolmodifystation2");
	}

	@Override
	public void initGui() {
		super.initGui();
		this.xSize = 176 + 110;
		this.guiLeft = (this.width - 176) / 2 - 110;
	}

	void setSlotType(int type) {
		switch (type) {
			case 0:
				slotX = new int[]{56, 38, 38}; // Repair
				slotY = new int[]{37, 28, 46};
				break;
			case 1:
				slotX = new int[]{56, 56, 56}; // Three parts
				slotY = new int[]{19, 55, 37};
				break;
			case 2:
				slotX = new int[]{56, 56, 14}; // Two parts
				slotY = new int[]{28, 46, 37};
				break;
			case 3:
				slotX = new int[]{38, 47, 56}; // Double head
				slotY = new int[]{28, 46, 28};
				break;
			case 7:
				slotX = new int[]{56, 56, 56}; // Three parts reverse
				slotY = new int[]{19, 37, 55};
				break;
		}
		toolSlots.resetSlots(slotX, slotY);
	}


	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.getInvName()), 116, 8, 0x000000);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 118, this.ySize - 96 + 2, 0x000000);

		ItemStack stackInSlot = logic.getStackInSlot(0);
		if (stackInSlot == null)
			stackInSlot = logic.getStackInSlot(1);
		if (stackInSlot != null && stackInSlot.hasTagCompound()) {
			ToolStationGuiHelper.drawToolStats(stackInSlot, 294, 0);
		} else
			drawToolInformation();
	}

	protected void drawToolInformation() {
		this.drawCenteredString(fontRendererObj, title, 349, 8, 0xffffff);
		fontRendererObj.drawSplitString(body, 294, 24, 115, 0xffffff);
	}

	private static final ResourceLocation background = new ResourceLocation(TinkersTailorConstants.RESOURCE_FOLDER, "textures/toolmodifystation.png");
	private static final ResourceLocation icons = new ResourceLocation("tinker", "textures/gui/icons.png");
	private static final ResourceLocation description = new ResourceLocation("tinker", "textures/gui/description.png");

	/**
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		// Draw the background
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		final int cornerX = this.guiLeft + 110;
		this.drawTexturedModalRect(cornerX, this.guiTop, 0, 0, 176, this.ySize);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(icons);
		// Draw the slots
		for (int i = 0; i < slotX.length; i++) {
			this.drawTexturedModalRect(cornerX + slotX[i], this.guiTop + slotY[i], 144, 216, 18, 18);
			if (!logic.isStackInSlot(i + 1)) {
				this.drawTexturedModalRect(cornerX + slotX[i], this.guiTop + slotY[i], 18 * iconX[i], 18 * iconY[i], 18, 18);
			}
		}

		// Draw description
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(description);
		this.drawTexturedModalRect(cornerX + 176, this.guiTop, 0, 0, 126, this.ySize + 30);
	}

	@Override
	public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) {
		currentVisibility.showWidgets = width - xSize >= 107;

		if (guiLeft < 58) {
			currentVisibility.showStateButtons = false;
		}

		return currentVisibility;
	}

	@Override
	public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
		return null;
	}

	@Override
	public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
		return Collections.emptyList();
	}

	@Override
	public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
		return false;
	}

	@Override
	public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
		if (y + h - 4 < guiTop || y + 4 > guiTop + ySize)
			return false;

		return x + 4 <= guiLeft + xSize + 126;

	}
}
