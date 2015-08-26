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
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package nz.govt.natlib.meta.ui;

import java.awt.Dimension;
import java.awt.Point;

import nz.govt.natlib.meta.HarvestEvent;
import nz.govt.natlib.meta.HarvestStatus;
import nz.govt.natlib.meta.Harvester;
import nz.govt.natlib.meta.MetaUtil;
import nz.govt.natlib.meta.ProgressListener;
import nz.govt.natlib.meta.ProgressStopListener;
import nz.govt.natlib.meta.PropertySource;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.tree.BaseNode;
import nz.govt.natlib.meta.ui.tree.FileModel;
import nz.govt.natlib.meta.ui.tree.FileTree;
import nz.govt.natlib.meta.ui.tree.ObjectFolderNode;
import nz.govt.natlib.meta.ui.tree.Property;
import nz.govt.natlib.meta.ui.tree.RootNode;

/**
 * This runnable class manages the harvesting of metadata. It constructs an
 * appropriate 'harvester' for every type of object/config combination and sets
 * the job going.
 * 
 * @author unascribed
 * @version 1.0
 */

public class HarvestProcess implements Runnable, ProgressStopListener,
		ProgressListener {

	private FileModel fileList;

	private FileTree fileTree;

	private boolean stopping = false;

	private int totalFiles;

	private long totalBytes;

	private long processedBytes;

	private int processedFileCount;

	private Progress prog;

	private Harvester harvester = null;

	private Main mainFrame;

	private Configuration config;

	private long startTime = 0;

	private long lastEstimate = Long.MAX_VALUE;

	private boolean error = false;

	public HarvestProcess(Main mainFrame, FileTree fileTree,
			Configuration config) {
		this.fileList = (FileModel) fileTree.getModel();
		this.fileTree = fileTree;
		this.mainFrame = mainFrame;
		this.config = config;
	}

	public void progressEvent(Object subject) {
		if (stopping)
			return;

		// mark off some sort of progress...
		HarvestEvent event = (HarvestEvent) subject;
		processedFileCount++;

		if (event.isSucessful()) {
			event.getSource().setStatus(HarvestStatus.OK, null);
		} else {
			// find the error and 'mark' it
			Object source = event.getError();
			String comment = "";
			if (source instanceof Throwable) {
				comment = ((Throwable) source).getMessage();
			}
			LogMessage msg = new LogMessage(LogMessage.ERROR, source,
					"Error harvesting file :"
							+ event.getSource().getFile().getName(), comment);
			LogManager.getInstance().logMessage(msg);
			event.getSource().setStatus(HarvestStatus.ERROR,
					comment + " <b>(log id=" + msg.getId() + ")</b>");
			error = true;
		}

		String name = event.getSource().getFile().getName();
		processedBytes += event.getSource().getFile().length();
		// fancy bit... Work out how long it's taken so far - how long it will
		// be yet!
		long msEstimate = -1;
		if (processedFileCount > 50) {
			long ms = System.currentTimeMillis() - startTime;
			double msPerFile = ms / (double) processedFileCount;
			long workingEstimate = (long) (msPerFile * (totalFiles - processedFileCount));
			// the estimate is only allowed to go down - unless there is a 20sec
			// upwards re-estimate
			if ((workingEstimate < lastEstimate)
					|| (workingEstimate > lastEstimate + 20000)) {
				lastEstimate = msEstimate = workingEstimate;
			} else {
				msEstimate = lastEstimate;
			}
		}

		// show progress.
		prog
				.setProgress(
						"Processing: "
								+ name
										.substring(0, Math.min(name.length(),
												40)),
						"Files: " + processedFileCount + " of " + totalFiles,
						msEstimate > -1 ? "Estimated time remaining: "
								+ MetaUtil.formatMs(msEstimate) : " ",
						(int) (((double) processedFileCount / (double) totalFiles) * 100));
	}

	/**
	 * count the tree model...
	 */
	private void fileCount(BaseNode node) {
		if (stopping)
			return;

		if (node.isLeaf()) {
			totalFiles++;
			totalBytes += node.getFile().length();
			// Updating this takes more time tan adding the files - so only do
			// it occasionally
			boolean update = (totalFiles % 34) == 7; // air of randomness!
			if (update) {
				prog.setProgress("Preparing to Process", "Files: " + 0 + " of "
						+ totalFiles, " ", 0);
			}
		} else {
			for (int i = 0; i < node.getChildCount() && !stopping; i++) {
				fileCount((BaseNode) node.getChildAt(i));
			}
		}
	}

	public void run() {
		// show the progress window
		// set the cursor to be an hourglass...
		mainFrame.setBusy(true);

		prog = new Progress(mainFrame, true);
		prog.setProgressStopListener(this);

		prog.setSize((int) prog.getPreferredSize().getWidth() + 150, (int) prog
				.getPreferredSize().getHeight() + 20);
		prog.setResizable(false);

		// set the location.
		Point p1 = mainFrame.getLocation();
		Dimension d1 = mainFrame.getSize();
		Dimension d2 = prog.getSize();
		prog.setLocation((p1.x + (d1.width / 2)) - (d2.width / 2),
				(p1.y + (d1.height / 2)) - (d2.height / 2));
		prog.setProgress("Preparing to Process", " ", " ", 0);
		prog.setVisible(true);

		// temporarily stop all events on the tree...
		fileList.suspendEvents(true);

		// how many files are to be processed?
		fileCount((BaseNode) fileList.getRoot());

		startTime = System.currentTimeMillis();
		RootNode root = (RootNode) fileList.getRoot();
		for (int i = 0; i < root.getChildCount() && !stopping; i++) {
			ObjectFolderNode object = (ObjectFolderNode) root.getChildAt(i);
			// work out what kind of harvester to use for each object...

			if (!FileModel.hasAFile(object)) {
				LogMessage msg = new LogMessage(LogMessage.DEBUG, object
						.getName(), "Object contains no files - skipping",
						"Add files to this object");
				LogManager.getInstance().logMessage(msg);
				continue;
			}

			String harvesterClass = config.getClassName();
			if (harvesterClass != null) {
				try {
					harvester = (Harvester) Class.forName(harvesterClass)
							.newInstance();
					// go to it...
					System.out.println("Harvesting " + object);
					harvester.harvest(config, object, new PropsManager(object,
							mainFrame), this);
				} catch (Throwable ex) {
					System.out.println("Exception Harvesting " + object);
					LogMessage msg = new LogMessage(LogMessage.ERROR, ex, ex
							.getMessage(), "");
					LogManager.getInstance().logMessage(msg);
					object.setStatus(HarvestStatus.ERROR, ex.getMessage()
							+ " (logid=" + msg.getId() + ")");
					error = true;
				}
				System.gc();
			} else {
				LogMessage msg = new LogMessage(
						LogMessage.ERROR,
						null,
						"No harvester class specified for " + config.getName(),
						"check the config file, there should be a\n<harvester class='<classname>'/> tag");
				LogManager.getInstance().logMessage(msg);
			}
		}

		fileList.suspendEvents(false);
		fileList.nodeStructureChanged(root); // now that events are enabled,
												// fire one for all changes
		fileTree.repaint();
		prog.setVisible(false);
		prog.dispose();

		mainFrame.setStatus(error == true ? HarvestStatus.ERROR
				: HarvestStatus.OK);
		mainFrame.setBusy(false);
		LogManager.getInstance()
				.logMessage(LogMessage.INFO, "Harvest Complete");
	}

	public void stop() {
		if (harvester != null) {
			harvester.stop();
		}

		prog.setProgress("Stopping processing, cleaning up", " ", " ", 100);

		stopping = true;
	}

	private class PropsManager implements PropertySource {

		private ObjectFolderNode rootContext;

		private PropertySource appContext;

		private PropsManager(ObjectFolderNode rootContext,
				PropertySource appContext) {
			this.rootContext = rootContext;
			this.appContext = appContext;
		}

		public Object getProperty(String property, String condition) {
			Object result = null;

			// there are some values that are not stored as properties.
			if (property.equalsIgnoreCase("type")) {
				result = rootContext.getType() == ObjectFolderNode.COMPLEX ? "complex"
						: "simple";
			}

			// check the parent
			Property[] props = rootContext.getProperties();
			Property prop = null;
			for (int i = 0; i < props.length; i++) {
				if (props[i].getName().toLowerCase().equals(
						property.toLowerCase())) {
					prop = props[i];
					break;
				}
			}
			if (prop != null) {
				result = rootContext.getPropertyValue(prop);
			}

			// check app context...
			/**
			 * 
			 */
			if (result == null) {
				result = appContext.getProperty(property, condition);
			}

			return result;
		}

	}
}
