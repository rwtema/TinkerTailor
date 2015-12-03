package test;

import com.rwtema.tinkertailor.TinkersTailor;
import com.rwtema.tinkertailor.compat.ModCompatibilityModule;
import com.rwtema.tinkertailor.imc.IMCHandler;
import com.rwtema.tinkertailor.imc.IMCNBTLoader;
import com.rwtema.tinkertailor.nbt.StringHelper;
import cpw.mods.fml.common.LoaderException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import org.apache.logging.log4j.core.helpers.Strings;

@ModCompatibilityModule.InitialiseMe(requiredMods = "")
public class Test extends ModCompatibilityModule {
	@Override
	public void onCreated() {
		if (!TinkersTailor.deobf)
			throw new LoaderException("DEOBF-only Code present in jar file");

		IMCHandler.init();

		PrintWriter out = null;
		try {
			try {
				File file = getFile();
				if (file.getParentFile() != null) {
					file.getParentFile().mkdirs();
				}

				out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

				for (Map.Entry<String, IMCNBTLoader> entry : IMCNBTLoader.nbtLoaders.entrySet()) {
					addHeaderLine(out);
					IMCNBTLoader value = entry.getValue();
					out.println(value.desc);
					out.println("");
					out.println("IMC Key = " + entry.getKey());
					out.println("");
					out.println("NBT Values");

					ArrayList<String> strings = new ArrayList<String>();
					strings.add("Key\tType");
					for (Map.Entry<Field, IMCNBTLoader.IMCFieldHandler> fEntry : value.fields.entrySet()) {
						Field key = fEntry.getKey();
						StringBuilder builder = new StringBuilder().append(key.getName()).append("\t").append(StringHelper.capFirst(key.getType().getSimpleName()));
						if (Strings.isNotEmpty(fEntry.getValue().expectedType)) {
							builder.append("(").append(fEntry.getValue().expectedType).append(")");
						}

						if (value.required.contains(key.getName())) {
							builder.append("\trequired");
						} else {
							builder.append("\toptional");
							Object o = value.defaults.get(key);
							if (o != null)
								builder.append("(default = ").append(o).append(")");
						}
						strings.add(builder.toString());
					}

					for (String string : StringHelper.formatTabsToTableSpaced(strings)) {
						out.println(string);
					}

				}
				addHeaderLine(out);
			} finally {
				if (out != null)
					out.close();
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void addHeaderLine(PrintWriter out) {
		out.println("//---------------------------------------------------//");
	}

	private static File getFile() {
		return new File(new File(new File("."), "debug_text"), "IMC_Doc.txt");
	}
}
