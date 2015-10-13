package com.rwtema.tinkertailor.manual;

import com.rwtema.tinkertailor.modifiers.Modifier;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.modifiers.itemmodifier.ModOreModifier;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;

public class PageModifier extends PageBase {

	String text;
	Modifier modifier;
	String desc;

	@Override
	protected void render(int localWidth, int localHeight, boolean isTranslatable) {

		drawCenteredString("\u00a7n" + text, 4, localWidth, localHeight);

		List stacks = modifier.itemModifier.stacks;
		if (!stacks.isEmpty()) {
			startRenderingItem();
			ItemStack itemStack = (ItemStack) stacks.get(curTime() % stacks.size());
			if (itemStack != null) {
				if (modifier.itemModifier instanceof ModOreModifier) {
					String s = "Value: " + ((ModOreModifier) modifier.itemModifier).getValue(itemStack);

					int x = (178 - 18 - 2 - manual.fonts.getStringWidth(s)) / 2;
					renderStack(itemStack, x, 20, localWidth, localHeight);
					manual.fonts.drawString(s, localWidth + x + 20, localHeight + 24, 0);
				} else
					renderStack(itemStack, 81, 20, localWidth, localHeight);
			}
			stopRenderingItem();
		}

		int yPos = 42;
		yPos += drawTextBlock(desc, 4, yPos, localWidth, localHeight);
		yPos += drawBlankLine();
		for (String s : modifier.getDocLines()) {
			yPos += drawTextBlock(s, 4, yPos, localWidth, localHeight);
		}
		yPos += drawTextLine("- Max Level: " + (modifier.getMaxLevel() / modifier.getModifierStep()), 4, yPos, localWidth, localHeight);

		yPos += drawTextLine("- Cost per Level: " + modifier.getModifierStep(), 4, yPos, localWidth, localHeight);
		yPos += drawBlankLine();
		int allowedArmorTypes = modifier.allowedArmorTypes;
		String restText;
		if (allowedArmorTypes == 0) {
			restText = "None";
		} else {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < 4; i++) {
				if ((allowedArmorTypes & (1 << i)) == 0) {
					builder.append(TinkersTailorConstants.NAMES[i]).append(' ');
				}
			}
			builder.append("Only");
			restText = builder.toString();
		}

		drawTextLine("- Restrictions: " + restText, 4, yPos, localWidth, localHeight);

	}


	@Override
	protected void loadData() throws IOException {
		text = loadText("text");
		String modifierName = loadText("modifier");
		modifier = ModifierRegistry.modifiers.get(modifierName);
		assert modifier != null;

		desc = loadText("desc");
	}
}
