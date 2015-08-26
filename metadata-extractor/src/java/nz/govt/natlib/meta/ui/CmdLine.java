/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package nz.govt.natlib.meta.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.Configurator;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.meta.HarvestEvent;
import nz.govt.natlib.meta.HarvestSource;
import nz.govt.natlib.meta.HarvestStatus;
import nz.govt.natlib.meta.Harvester;
import nz.govt.natlib.meta.ProgressListener;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.ConfigMapEntry;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.config.Profile;
import nz.govt.natlib.meta.log.Log;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * eg. extract "NLNZ Data Dictionary" simple recurse "c:\Temp"
 * @author Nic Evans
 * @version 1.0
 */

public class CmdLine {

	private static final String HELP_MODE = "help";

	private static final String SHOW_MODE = "show";

	private static final String EXTRACT_MODE = "extract";

	public static final String SIMPLE_PARAM = "simple";

	public static final String COMPLEX_PARAM = "complex";

	public static final String RECURSE_PARAM = "recurse";

	public static final String FLATTEN_PARAM = "flatten";

	private static final String CONFIG_PARAM = "config";

	private static final String PROFILE_PARAM = "profile";

	private static final String ADAPTER_PARAM = "adapters";

	private static final String MAP_PARAM = "maps";

	public static void main(String[] args) {
		Configurator.install(false);
		// the first parameter is the mode...
		LogManager.getInstance().addLog(new CmdLogger());

		boolean showHelp = true;
		if ((args == null) || (args.length < 1)) {
			// just show the help...
			showHelp = true;
		} else {
			String mode = args[0];
			if (HELP_MODE.equalsIgnoreCase(mode)) {
				showHelp = true;
			} else if (SHOW_MODE.equalsIgnoreCase(mode)) {
				if (args.length < 2) {
					System.out.println("Parameter required for " + SHOW_MODE
							+ " mode");
					showHelp = true;
				} else {
					String param = args[1];
					showHelp = !showConfig(param);
				}
			} else if (EXTRACT_MODE.equalsIgnoreCase(mode)) {

				if (args.length < 6) {
					System.out
							.println("You are missing some key parameters for "
									+ EXTRACT_MODE + " mode\n");
					showHelp = true;
				} else {
					boolean flatten = false;
					boolean recurse = false;
					String file = null;
					String config = args[1];
					String profile = args[2];
					String object = args[3].toLowerCase();
					String objectName = args[4];
					String objectID = args[5];

					for (int i = 6; i < args.length; i++) {
						String arg = args[i];
						if (arg.equalsIgnoreCase(RECURSE_PARAM)) {
							recurse = true;
						} else if (arg.equalsIgnoreCase(FLATTEN_PARAM)) {
							flatten = true;
						} else {
							file = arg;
						}
					}

					showHelp = !extract(config, profile, object, objectName,
							objectID, file, recurse, flatten);
				}
			} else {
				System.out.println("Invalid mode");
				showHelp = true;
			}
		}
		if (showHelp) {
			showHelp();
		}
	}

	private static boolean extract(String config, String profile,
			String objectType, String name, String id, String file,
			boolean recurse, boolean flatten) {
		boolean handled = false;

		ArrayList profiles = Config.getInstance().getAvailableProfiles();
		Iterator pf = profiles.iterator();
		Profile prof = null;
		while (pf.hasNext()) {
			Profile p = (Profile) pf.next();
			if (p.getName().equalsIgnoreCase(profile)) {
				prof = p;
				Config.getInstance().setCurrentProfile(p);
			}
		}

		// find the config requested
		ArrayList configs = Config.getInstance().getAvailableConfigs();
		Iterator it = configs.iterator();
		Configuration configuration = null;
		while (it.hasNext()) {
			Configuration c = (Configuration) it.next();
			if (c.getName().equalsIgnoreCase(config)) {
				configuration = c;
			}
		}
		if (prof != null) {
			handled = true;
			System.out.println("Application Base XML directrory :"
					+ Config.getInstance().getXMLBaseURL());
			System.out.println("Selected Config :" + configuration.getName());
			System.out.println("  Name: " + configuration.getName());
			System.out.println("  Output Directory: "
					+ configuration.getOutputDirectory());
			System.out.println("  Output DTD: " + configuration.getOutputDTD());
			System.out.println();

			// process for each file in the list...
			File root = new File(file);
			int type = objectType.equalsIgnoreCase(SIMPLE_PARAM) ? HarvestSource.SIMPLE
					: HarvestSource.COMPLEX;
			FileList source = getFiles(root, flatten, recurse);
			source.setType(type);

			// organise the properties...
			PropsManager props = new PropsManager();
			props.setProperty("type", objectType);
			props.setProperty("Name", name);
			props.setProperty("ID", id);

			// process...
			processFiles(configuration, source, props);
		} else {
			System.out.println("Configuration: " + config + " not installed");
		}

		return handled;
	}

	private static void processFiles(Configuration config,
			HarvestSource source, PropsManager props) {
		String harvesterClass = config.getClassName();
		if (harvesterClass != null) {
			try {
				LogManager.getInstance().logMessage(
						LogMessage.WORTHLESS_CHATTER, "Harvesting files");
				Harvester harvester = (Harvester) Class.forName(harvesterClass)
						.newInstance();
				// go to it...
				LogManager.getInstance().logMessage(
						LogMessage.WORTHLESS_CHATTER,
						"Using :" + harvester.getClass().getName());
				harvester.harvest(config, source, props,
						new HarvestProgressListener());
				LogManager.getInstance().logMessage(
						LogMessage.WORTHLESS_CHATTER, "Harvesting complete");
			} catch (Exception ex) {
				LogMessage msg = new LogMessage(LogMessage.ERROR, ex, ex
						.getMessage(), "");
				LogManager.getInstance().logMessage(msg);
				source.setStatus(HarvestStatus.ERROR, ex.getMessage()
						+ " (logid=" + msg.getId() + ")");
			}
		} else {
			LogMessage msg = new LogMessage(
					LogMessage.ERROR,
					null,
					"No harvester class specified for " + config.getName(),
					"check the config file, there should be a\n<harvester class='<classname>'/> tag");
			LogManager.getInstance().logMessage(msg);
		}
	}

	private static FileList getFiles(File file, boolean flatten, boolean recurse) {
		if (file.isDirectory()) {
			FileList list = new FileList(file, 0);
			if (recurse) {
				File[] f = file.listFiles();
				for (int i = 0; i < f.length; i++) {
					list.addFile(getFiles(f[i], flatten, recurse));
				}
			}
			return list;
		} else {
			return new FileList(file, 0);
		}
	}

	private static class FileList implements HarvestSource {
		private File file;

		private ArrayList children;

		private int type;

		private FileList(File file, int type) {
			this.type = type;
			this.file = file;
			children = new ArrayList();
		}

		public void addFile(FileList file) {
			children.add(file);
		}

		public HarvestSource[] getChildren() {
			if (children.size() == 0)
				return null; // required for harvesters to work properly

			HarvestSource[] result = new HarvestSource[children.size()];
			children.toArray(result);
			return result;
		}

		public String getName() {
			return file.getName();
		}

		public File getFile() {
			return file;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public void setStatus(HarvestStatus status, String message) {
			// no care...
			if (status == HarvestStatus.ERROR) {
				LogManager.getInstance().logMessage(LogMessage.ERROR, message);
			}
			if (status == HarvestStatus.OK) {
				LogManager.getInstance().logMessage(
						LogMessage.WORTHLESS_CHATTER, message);
			}
		}

		private String toString(String indent) {
			StringBuffer buf = new StringBuffer(indent + getName() + "\n");
			for (int i = 0; i < children.size(); i++) {
				FileList child = (FileList) children.get(i);
				buf.append(child.toString(indent + "  "));
			}
			return buf.toString();
		}

		public String toString() {
			return toString("");
		}
	}

	private static boolean showConfig(String param) {
		boolean handled = false;
		System.out.println("\nApplication Base XML directrory :"
				+ Config.getInstance().getXMLBaseURL() + "\n");
		if (CONFIG_PARAM.equalsIgnoreCase(param)) {
			System.out.println("Available Configurations");
			ArrayList configs = Config.getInstance().getAvailableConfigs();
			Iterator it = configs.iterator();
			while (it.hasNext()) {
				Configuration c = (Configuration) it.next();
				System.out.println("  Config:");
				System.out.println("    Name: " + c.getName());
				System.out.println("    Output Directory: "
						+ c.getOutputDirectory());
				System.out.println("    Output DTD: " + c.getOutputDTD());
			}
			handled = true;
		} else if (PROFILE_PARAM.equalsIgnoreCase(param)) {
			System.out.println("Available Profiles");
			ArrayList profiles = Config.getInstance().getAvailableProfiles();
			Iterator it = profiles.iterator();
			while (it.hasNext()) {
				Profile p = (Profile) it.next();
				System.out.println("  Profile:");
				System.out.println("    Name: " + p.getName());
				System.out.println("    Input Directory: "
						+ p.getInputDirectory());
				System.out.println("    Log Directory: " + p.getLogDirectory());
				System.out.println("    Adapters switched on:");
				Iterator adapters = p.getAdapterClasses();
				while (adapters.hasNext()) {
					String adClass = (String) adapters.next();
					DataAdapter da = AdapterFactory.getInstance().getAdapter(
							adClass);
					if (da != null) {
						System.out.println("      " + da.getName() + " "
								+ da.getVersion());
						System.out.println("      Desc: " + da.getDescription()
								+ ": ");
					}
				}
			}
			handled = true;
		} else if (ADAPTER_PARAM.equalsIgnoreCase(param)) {
			System.out.println("Installed Adapters (in order)");
			DataAdapter[] adapters = AdapterFactory.getInstance().getAdapters();
			for (int i = 0; i < adapters.length; i++) {
				System.out.println("  Adapter:");
				System.out.println("    Input: " + adapters[i].getInputType());
				System.out
						.println("    Output: " + adapters[i].getOutputType());
			}
			handled = true;
		} else if (MAP_PARAM.equalsIgnoreCase(param)) {
			System.out.println("Installed XSLT maps");
			ConfigMapEntry[] maps = Config.getInstance().getMappings();
			for (int i = 0; i < maps.length; i++) {
				System.out.println("  Map:");
				System.out.println("    Input: " + maps[i].getInputDTD());
				System.out.println("    Output: " + maps[i].getOutputDTD());
				System.out.println("    XSLT: " + maps[i].getXsltFunction());
			}
			handled = true;
		} else {
			System.out.println("Invalid parameter for " + SHOW_MODE + " mode");
		}
		return handled;
	}

	private static void showHelp() {
		System.out.println("Usage: ");
		System.out.println("  " + HELP_MODE + "\t\t - help");
		System.out.println("  " + SHOW_MODE + " " + CONFIG_PARAM
				+ "\t - show the available configurations");
		System.out.println("  " + SHOW_MODE + " " + PROFILE_PARAM
				+ "\t - show the available profiles");
		System.out.println("  " + SHOW_MODE + " " + ADAPTER_PARAM
				+ "\t - show the adapters installed");
		System.out.println("  " + SHOW_MODE + " " + MAP_PARAM
				+ "\t - show the xslt maps installed");
		System.out.println("  " + EXTRACT_MODE + " <" + CONFIG_PARAM + "> <"
				+ SIMPLE_PARAM + "|" + COMPLEX_PARAM + "> <Name> <ID> <"
				+ RECURSE_PARAM + "> <" + FLATTEN_PARAM + "> <files...>");
		System.out.println("      " + CONFIG_PARAM
				+ "\t - the config to process with, from the config.xml");
		System.out.println("      " + PROFILE_PARAM
				+ "\t - the profile to process with, from the config.xml");
		System.out.println("      " + SIMPLE_PARAM
				+ "\t - process using the simple object paradigm");
		System.out.println("      " + COMPLEX_PARAM
				+ "\t - process using the complex object paradigm");
		System.out
				.println("      Name\t\t - The name of the object being extracted");
		System.out
				.println("      ID\t\t - The ID of the object being extracted");
		System.out.println("      " + RECURSE_PARAM
				+ "\t - recurse directories during processing");
		System.out.println("      " + FLATTEN_PARAM
				+ "\t - if recursing directories, flatten the structure");
		System.out
				.println("      files...\t - the files to be processed (multiple files, wildcards, etc...)");
		System.out.println("\n      Some examples of usage:");
		System.out.println("      \textract.bat " + EXTRACT_MODE
				+ " \"NLNZ Data Dictionary\" " + SIMPLE_PARAM
				+ " \"An Object\" 2003 " + RECURSE_PARAM + " " + FLATTEN_PARAM
				+ " \"c:\\temp\\downloads\"");
		System.out.println("      \textract.bat " + EXTRACT_MODE
				+ " \"NLNZ Data Dictionary\" " + COMPLEX_PARAM
				+ " \"Complex One\" 667 " + RECURSE_PARAM
				+ " \"c:\\temp\\downloads\"");
		System.out
				.println("\n      \tNote: Quotes are useful when the parameter you use has spaces in it, i.e. the "
						+ CONFIG_PARAM + " parameter");
	}

	public static class CmdLogger implements Log {
		public void logMessage(LogMessage message) {
			if (message.getLevel().getLevel() >= LogMessage.WORTHLESS_CHATTER
					.getLevel()) {
				System.out.print(message.getLevel().getName() + ": "
						+ message.getId() + ", " + message.getMessage());
				if (message.getSource() instanceof Throwable) {
					System.out.print(": "
							+ (((Throwable) message.getSource()).getMessage()));
				}
				System.out.println();
			}
		}

		public void close() {
		}

		public void suspendEvents(boolean suspend) {
		}
	}

	private static class HarvestProgressListener implements ProgressListener {

		public void progressEvent(Object subject) {
			HarvestEvent event = (HarvestEvent) subject;
			if (!event.isSucessful()) {
				LogManager.getInstance().logMessage(
						(Throwable) event.getError());
			}
		}

	}
}