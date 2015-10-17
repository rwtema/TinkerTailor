package com.rwtema.tinkertailor.manual;

import com.rwtema.tinkertailor.utils.Lang;
import gnu.trove.list.array.TIntArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PageContents extends PageBase {

	TIntArrayList pages = new TIntArrayList();
	ArrayList<String> contents = new ArrayList<String>();

	@Override
	protected void render(boolean isTranslatable) {
		int yPos = 4;
		yPos += drawCenteredString(Lang.translate("Contents"), yPos);
		yPos += drawBlankLine() * 2;
		for (int i = 0; i < pages.size(); i++) {
			String pageTxt = Lang.translate("Page") + " " + pages.get(i);
			int x = PAGE_WIDTH - 4 - getStringWidth(pageTxt);

			int mY = drawTextLine(pageTxt, x, yPos);
			mY = Math.max(mY, drawTextBlock(contents.get(i), 4, yPos, x - 12));


			int s = 0;
			List<String> splitStr = manual.fonts.listFormattedStringToWidth(contents.get(i), x - 12);
			int BORDER = 2;
			for (String s1 : splitStr) {
				s = Math.max(s, BORDER + getStringWidth(s1));
			}

			int pw = manual.fonts.getStringWidth(".");
			if (s + pw < (x - BORDER)) {
				for (int nx = s; nx < (x - BORDER); nx += pw) {
					manual.fonts.drawString(".", nx, yPos, 0);
				}
			}
			yPos += mY;
		}

	}

	@Override
	protected void loadData() throws IOException {
		Document data = element.getOwnerDocument(); //((GuiCustomManual) manual).data.getDoc();
		NodeList nList = data.getElementsByTagName("page");
		int page_no = 0;
		for (int i = 0; i < nList.getLength(); i++) {
			Node node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				page_no++;
				Element element = (Element) node;
				String type = element.getAttribute("type");
				if ("tailorSection".equals(type)) {
					Node nodes = element.getElementsByTagName("section").item(0);
					if (nodes != null) {
						String section = nodes.getTextContent();
						pages.add(page_no);
						contents.add(section);
					}
				}
			}
		}

	}
}
