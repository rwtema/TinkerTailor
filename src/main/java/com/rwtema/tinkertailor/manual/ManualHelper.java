package com.rwtema.tinkertailor.manual;

import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import java.io.InputStream;
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

public class ManualHelper implements IResourceManagerReloadListener {
	public static BookData bookData;
	public static ManualHelper instance =new ManualHelper();

	public static void init(){
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(instance);
		MProxyClient.registerManualPage("tailorTitle", PageTitle.class);
		MProxyClient.registerManualPage("tailorSection", PageSectionText.class);
		MProxyClient.registerManualPage("tailorModifier", PageModifier.class);

//		BookDataStore.addBook(bookData);
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
