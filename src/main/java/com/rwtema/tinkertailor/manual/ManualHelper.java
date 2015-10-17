package com.rwtema.tinkertailor.manual;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.modifiers.ModifierRegistry;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import java.io.InputStream;
import java.util.HashSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import mantle.books.BookData;
import mantle.client.MProxyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ManualHelper implements IResourceManagerReloadListener {
	public static BookData bookData;
	public static ManualHelper instance = new ManualHelper();

	public static void init() {
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(instance);
		MProxyClient.registerManualPage("tailorTitle", PageTitle.class);
		MProxyClient.registerManualPage("tailorSection", PageSectionText.class);
		MProxyClient.registerManualPage("tailorModifier", PageModifier.class);
		MProxyClient.registerManualPage("tailorBlockCast", PageBlockCast.class);
		MProxyClient.registerManualPage("tailorContents", PageContents.class);
	}

	public static void reloadBookData() {
		bookData = new BookData();
		bookData.unlocalizedName = "tinkersTailor.manual";
		bookData.toolTip = "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip");
		bookData.modID = TinkersTailorConstants.MOD_ID;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		String CurrentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		Document tailorBook = readManual("manuals/" + CurrentLanguage + "/tailor.xml", dbFactory);
		bookData.doc = tailorBook != null ? tailorBook : readManual("manuals/en_US/tailor.xml", dbFactory);

		if (bookData.doc == null) throw new RuntimeException("Missing Book Data");

		if (TinkersTailor.deobf) {
			HashSet<String> names = new HashSet<String>(ModifierRegistry.modifiers.keySet());
			NodeList nList = bookData.doc.getElementsByTagName("page");

			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					String type = element.getAttribute("type");
					if ("tailorModifier".equals(type)) {
						Node nodes = element.getElementsByTagName("modifier").item(0);
						if (nodes != null) {
							String modifier = nodes.getTextContent();
							names.remove(modifier);
						}
					}
				}
			}

			for (String name : names) {
				if (ModifierRegistry.modifiers.get(name).shouldHaveDocEntry()) {
					TinkersTailor.logger.info("Missing modifer entry for " + name);
				}
			}
		}
	}

	static Document readManual(String location, DocumentBuilderFactory dbFactory) {

		try {
			ResourceLocation resourceLocation = new ResourceLocation(TinkersTailorConstants.RESOURCE_FOLDER, location);
			IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
			InputStream stream = null;
			try {
				stream = resource.getInputStream();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(stream);
				doc.getDocumentElement().normalize();
				return doc;
			} finally {
				if (stream != null)
					stream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager p_110549_1_) {
		reloadBookData();
	}
}
