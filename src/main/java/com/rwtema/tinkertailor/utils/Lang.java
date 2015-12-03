package com.rwtema.tinkertailor.utils;

import com.google.common.base.Throwables;
import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.nbt.TinkersTailorConstants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;

public class Lang {
	private final static TreeMap<String, String> lang = TinkersTailor.deobf_folder ? new TreeMap<String, String>() : null;
	private final static HashMap<String, String> textKey = new HashMap<String, String>();
	private static final int MAX_KEY_LEN = 32;

	static {
		if (TinkersTailor.deobf_folder) {
			try {
				FileInputStream fis = null;
				try {
					File file = getFile();
					fis = new FileInputStream(file);
					HashMap<String, String> langMap = StringTranslate.parseLangFile(fis);
					lang.putAll(langMap);
				} finally {
					if (fis != null)
						fis.close();
				}
			} catch (FileNotFoundException ignore) {

			} catch (IOException e) {
				e.printStackTrace();
			}

			ResourceLocation resourceLocation = new ResourceLocation(TinkersTailorConstants.RESOURCE_FOLDER, "lang/en_US.lang");
			try {
				IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
				InputStream stream = null;
				try {
					stream = resource.getInputStream();
					HashMap<String, String> langMap = StringTranslate.parseLangFile(stream);
					lang.putAll(langMap);
				} finally {
					if (stream != null)
						stream.close();
				}
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}


		}
	}

	public static String translate(String text) {
		return findTranslateKey(text, "text");
	}

	public static String findTranslateKey(String text, String prefix) {
		String key = getKey(text, prefix);
		return translate(key, text);
	}

	public static String getKey(String text) {
		return getKey(text, "text");
	}

	public static String getKey(String text, String prefix) {
		String key = textKey.get(text);
		if (key == null) {
			key = makeKey(text, prefix);
			textKey.put(text, key);
			if (TinkersTailor.deobf_folder) {
				translate(key, text);
			}
		}
		return key;
	}

	private static String makeKey(String text, String prefix) {
		String key;
		String t = text.replaceAll("([^A-Za-z\\s])", "").trim();
		t = t.replaceAll("\\s+", ".").toLowerCase();
		if (t.length() > MAX_KEY_LEN) {
			int n = t.indexOf('.', MAX_KEY_LEN);
			if (n != -1)
				t = t.substring(0, n);
		}
		key = "tinkersTailor." + prefix + "." + t;
		return key;
	}

	public static String translate(String key, String _default) {
		if (StatCollector.canTranslate(key))
			return StatCollector.translateToLocal(key);
		if (TinkersTailor.deobf_folder) {
			if (!_default.equals(lang.get(key))) {
				lang.put(key, _default);
				PrintWriter out = null;
				try {
					try {
						File file = getFile();
						if (file.getParentFile() != null) {
							file.getParentFile().mkdirs();
						}

						out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
						String t = null;
						for (Map.Entry<String, String> entry : lang.entrySet()) {
							int i = entry.getKey().indexOf('.');
							if (i < 0) {
								i = 1;
							}

							String s = entry.getKey().substring(0, i);
							if (t != null) {
								if (!t.equals(s)) {
									out.println("");
								}
							}
							t = s;

							out.println(entry.getKey() + "=" + entry.getValue());

						}
					} finally {
						if (out != null)
							out.close();
					}
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		}
		return _default;
	}

	private static File getFile() {
		return new File(new File(new File("."), "debug_text"), "missed_en_US.lang");
	}
}
