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
import java.io.File;

import nz.govt.natlib.meta.ProgressStopListener;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.tree.FileModel;
import nz.govt.natlib.meta.ui.tree.FileNode;
import nz.govt.natlib.meta.ui.tree.FolderNode;

/**
 * @author unascribed
 * @version 1.0
 */
public class FileAdderProcess implements Runnable, ProgressStopListener {

	private File[] files;

	private FolderNode into;

	private boolean recurseFolders;

	private boolean flattenFolders;

	private Progress prog;

	private int processedFileCount = 0;

	private int totalFiles = 0;

	private boolean stopping = false;

	private Main parent;

	private FileModel model;

	public FileAdderProcess(Main parent, FileModel model, FolderNode into,
			File[] files, boolean recurseFolders, boolean flattenFolders) {
		this.into = into;
		this.files = files;
		this.flattenFolders = flattenFolders;
		this.recurseFolders = recurseFolders;
		this.parent = parent;
		this.model = model;
		prog = new Progress(parent, true);
	}

	/**
	 * Adds new files to the designated folder, note this method can recurse
	 * through a heirarchy of folders if folders are present
	 * 
	 * @param file
	 * @param to
	 * @param flatten
	 */
	private void addFile(File file, FolderNode to, boolean recurse,
			boolean flatten) {
		// if it's a file create the file and add it...
		if (file.isFile()) {
			this.updateProgress(file);
			to.addNode(new FileNode(file));
		}
		// else, if it's a directory then add all of it's directories...
		if (file.isDirectory() && recurse) {
			FolderNode addToFolder = to;
			if (!flatten) {
				addToFolder = new FolderNode(file.getName());
				to.addNode(addToFolder);
			}
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length && !stopping; i++) {
					addFile(files[i], addToFolder, recurse, flatten);
				}
			}
		}
	}

	private void countFiles(File file, boolean recurseFolders) {
		// if it's a file create the file and add it...
		if (file.isFile()) {
			updateCount(file);
		}
		// else, if it's a directory then add all of it's directories...
		if (file.isDirectory() && recurseFolders) {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length && !stopping; i++) {
					countFiles(files[i], recurseFolders);
				}
			} else {
				LogManager.getInstance().logMessage(
						LogMessage.ERROR,
						"Unknown Folder Type:" + file.getName()
								+ ", cannot recurse");
			}
		}
	}

	public void run() {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Preparing to add files to: " + into.getName());
		prog.setProgressStopListener(this);
		prog.setSize((int) prog.getPreferredSize().getWidth() + 150, (int) prog
				.getPreferredSize().getHeight() + 20);
		prog.setResizable(false);

		// set the location.
		Point p1 = parent.getLocation();
		Dimension d1 = parent.getSize();
		Dimension d2 = prog.getSize();
		prog.setLocation((p1.x + (d1.width / 2)) - (d2.width / 2),
				(p1.y + (d1.height / 2)) - (d2.height / 2));
		parent.setBusy(true);
		prog.setVisible(true);

		try {
			// do the job - determine the scope of the problem...
			for (int i = 0; i < files.length && !stopping; i++) {
				countFiles(files[i], recurseFolders);
			}
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
					"Adding " + totalFiles + " files to: " + into.getName());

			// process the files...
			model.suspendEvents(true);
			for (int i = 0; i < files.length && !stopping; i++) {
				addFile(files[i], into, recurseFolders, flattenFolders);
			}
			model.suspendEvents(false);
			model.nodeStructureChanged(into); // now that events are enabled,
												// fire one for all changes
			LogManager.getInstance().logMessage(
					LogMessage.WORTHLESS_CHATTER,
					"Added " + processedFileCount + " files to: "
							+ into.getName());
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(LogMessage.ERROR,
					"Error while adding files to: " + into.getName());
			LogManager.getInstance().logMessage(ex);
		} finally {
			if (stopping) {
				// clear out the problem ones? I don't know if it should but
				// here's where you'd do it...
			}

			prog.setVisible(false);
			prog.dispose();

			parent.setBusy(false);
		}
	}

	public void updateCount(File file) {
		totalFiles++;

		// Updating this takes more time tan adding the files - so only do it
		// occasionally
		boolean update = (totalFiles % 34) == 1; // air of randomness!

		if (update) {
			String name = file.getName();
			prog.setProgress("Preparing to Add Files to: " + into.getName(),
					"Files: " + processedFileCount + " of " + totalFiles, " ",
					0);
		}
	}

	public void updateProgress(File file) {
		processedFileCount++;

		// Updating this takes more time tan adding the files - so only do it
		// occasionally
		boolean update = (processedFileCount % 34) == 1; // air of
															// randomness!

		if (update) {
			String name = file.getName();
			prog
					.setProgress(
							"Adding: "
									+ name.substring(0, Math.min(name.length(),
											40)),
							"Files: " + processedFileCount + " of "
									+ totalFiles,
							" ",
							(int) (((double) processedFileCount / (double) totalFiles) * 100));
		}
	}

	public void stop() {
		stopping = true;
	}

}
