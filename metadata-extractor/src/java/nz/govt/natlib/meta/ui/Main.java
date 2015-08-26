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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import nz.govt.natlib.Configurator;
import nz.govt.natlib.meta.HarvestStatus;
import nz.govt.natlib.meta.PropertySource;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.config.Profile;
import nz.govt.natlib.meta.config.ProfileListener;
import nz.govt.natlib.meta.config.User;
import nz.govt.natlib.meta.log.FileLogger;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.admin.AdminWindow;
import nz.govt.natlib.meta.ui.admin.ScheduleWindow;
import nz.govt.natlib.meta.ui.help.HelpWindow;
import nz.govt.natlib.meta.ui.log.LogModel;
import nz.govt.natlib.meta.ui.log.LogWindow;
import nz.govt.natlib.meta.ui.tree.BaseNode;
import nz.govt.natlib.meta.ui.tree.FileModel;
import nz.govt.natlib.meta.ui.tree.FileTree;
import nz.govt.natlib.meta.ui.tree.FolderNode;
import nz.govt.natlib.meta.ui.tree.ObjectFolderNode;
import nz.govt.natlib.meta.ui.tree.PropertyContainer;
import nz.govt.natlib.meta.ui.tree.PropertyEditorController;
import nz.govt.natlib.meta.ui.tree.RootNode;

/**
 * Main class to start the GUI.
 * 
 * @author unascribed
 * @version 1.0
 */
public class Main extends JFrame implements PropertySource {

	About about;

	JMenuBar jMenuBar1 = new JMenuBar();

	JMenu fileMenu = new JMenu();

	JMenuItem openMenuItem = new JMenuItem();

	JMenu helpMenu = new JMenu();

	JMenuItem aboutMenuItem = new JMenuItem();

	JMenuItem exitMenuItem = new JMenuItem();

	JMenu toolsMenu = new JMenu();

	JMenuItem harvestMenuItem = new JMenuItem();

	RootNode fileRoot = new RootNode();

	FileModel fileList = new FileModel(fileRoot);

	JPanel root = new JPanel();

	GridBagLayout gridBagLayout1 = new GridBagLayout();

	Border border1;

	TitledBorder titledBorder1;

	TitledBorder titledBorder2;

	JPanel configPanel = new JPanel();

	JScrollPane jScrollPane1 = new JScrollPane();

	JPanel filesPanel = new JPanel();

	TitledBorder titledBorder3;

	JPanel jPanel2 = new JPanel();

	JLabel configLabel = new JLabel();

	NLNZCombo configSelect = new NLNZCombo();

	NLNZCombo profileSelect = new NLNZCombo();

	JLabel profileLabel = new JLabel();

	GridBagLayout gridBagLayout2 = new GridBagLayout();

	BorderLayout borderLayout2 = new BorderLayout();

	GridBagLayout gridBagLayout3 = new GridBagLayout();

	ConfigModel configModel = new ConfigModel();

	Icon saveIcon;

	Icon newObjectIcon;

	Icon folder;

	Icon simpleFolder;

	Icon complexFolder;

	Icon groupFolder;

	TitledBorder titledBorder4;

	JLabel userLabel = new JLabel();

	JLabel destinationLabel = new JLabel();

	JTextField destinationText = new JTextField();

	NLNZCombo userSelect = new NLNZCombo();

	DefaultComboBoxModel userModel;

	DefaultComboBoxModel profileModel;

	JPopupMenu newObjectMenu = new JPopupMenu();

	JMenuItem complexMenuItem = new JMenuItem();

	JMenuItem simpleMenuItem = new JMenuItem();

	FileTree fileTree = null;

	JPopupMenu fileTreeMenu = new JPopupMenu();

	JMenuItem propsMenuItem2 = new JMenuItem();

	private LogWindow logWindow = null;

	private AdminWindow adminWindow = null;

	private ScheduleWindow scheduleWindow = null;

	private HelpWindow helpWindow = null;

	private LogModel logModel = new LogModel();

	JMenuItem logMenuItem = new JMenuItem();

	JMenuItem adminMenuItem = new JMenuItem();

	JMenuItem scheduleMenuItem = new JMenuItem();

	JMenuItem helpMenuItem = new JMenuItem();

	JMenu jMenu1 = new JMenu();

	JMenuItem cplxMenuItem = new JMenuItem();

	JMenuItem smplMenuItem = new JMenuItem();

	JMenuItem propsMenuItem = new JMenuItem();

	JMenuItem removeMenuItem = new JMenuItem();

	JMenuItem removeAllMenuItem = new JMenuItem();

	ToolBar toolBar = null;

	StatusBar statusBar = new StatusBar();

	JButton dummy = new JButton();

	// JButton removeUser = new JButton();
	Color normal = dummy.getBackground();

	Color over = null;

	ImageButton folderButton;

	PrintStream stdoutanderr = null;

	public Main() {
	}

	private void jbInit() throws Exception {
		folderButton = new ImageButton(new ImageIcon(ImagePanel
				.resolveImage("xp_folder_small.gif")));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		fileTree = new FileTree(fileList);
		// titledBorder4 = new
		// TitledBorder(BorderFactory.createEtchedBorder(Color.white,new
		// Color(148, 145, 140)),"Processing Results");
		setIconImage(ImagePanel.resolveImage("icon.gif"));
		newObjectIcon = new ImageIcon(ImagePanel
				.resolveImage("new_object_arrow.gif"));
		folder = new ImageIcon(ImagePanel.resolveImage("xp_folder_small.gif"));
		Icon addUserIcon = new ImageIcon(ImagePanel
				.resolveImage("add_user.gif"));
		Icon removeUserIcon = new ImageIcon(ImagePanel
				.resolveImage("delete_user.gif"));
		simpleFolder = new ImageIcon(ImagePanel
				.resolveImage("simple_folder.gif"));
		complexFolder = new ImageIcon(ImagePanel
				.resolveImage("complex_folder.gif"));
		toolBar = new ToolBar(this);
		over = new Color(normal.getRed() - 25, normal.getGreen() - 25, normal
				.getBlue() + 5);
		// addUser.setIcon(addUserIcon);
		// removeUser.setIcon(removeUserIcon);
		// folderButton.setIcon(folder);
		destinationText.setEditable(false);
		destinationText.setBackground(Color.white);
		border1 = BorderFactory.createEtchedBorder(Color.white, new Color(148,
				145, 140));
		titledBorder1 = new TitledBorder(border1, "Objects");
		// titledBorder3 = new
		// TitledBorder(BorderFactory.createEtchedBorder(Color.white,new
		// Color(148, 145, 140)),"Configuration");
		fileMenu.setMnemonic('F');
		fileMenu.setText("File");
		openMenuItem.setMnemonic('F');
		openMenuItem.setText("Add File/s");
		openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(81,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		openMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openMenuItem_actionPerformed(e);
			}
		});
		helpMenu.setMnemonic('H');
		helpMenu.setText("Help");
		aboutMenuItem.setMnemonic('B');
		aboutMenuItem.setText("About");
		aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aboutMenuItem_actionPerformed(e);
			}
		});
		exitMenuItem.setMnemonic('X');
		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitMenuItem_actionPerformed(e);
			}
		});
		toolsMenu.setMnemonic('T');
		toolsMenu.setText("Tools");
		harvestMenuItem.setMnemonic('P');
		harvestMenuItem.setText("Process");
		harvestMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(72,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		harvestMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				harvestMenuItem_actionPerformed(e);
			}
		});
		this.setJMenuBar(jMenuBar1);
		root.setLayout(gridBagLayout1);
		filesPanel.setLayout(gridBagLayout3);
		filesPanel.setFont(new java.awt.Font("Dialog", 1, 12));
		filesPanel.setBorder(titledBorder1);
		configPanel.setFont(new java.awt.Font("Dialog", 1, 12));
		configPanel.setLayout(borderLayout2);
		configLabel.setText("Config :");
		jPanel2.setLayout(gridBagLayout2);
		configSelect.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				configSelect_itemStateChanged(e);
			}
		});
		profileSelect.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				profileSelect_itemStateChanged(e);
			}
		});
		userSelect.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				userSelect_itemStateChanged(e);
			}
		});
		destinationLabel.setText("Destination :");
		profileLabel.setText("Profile :");
		userSelect.setEditable(true);
		userLabel.setText("User :");
		complexMenuItem.setMnemonic('X');
		complexMenuItem.setText("Complex Object");
		complexMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				complexMenuItem_actionPerformed(e);
			}
		});
		simpleMenuItem.setMnemonic('S');
		simpleMenuItem.setText("Simple Object");
		simpleMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simpleMenuItem_actionPerformed(e);
			}
		});
		propsMenuItem2.setText("Properties");
		propsMenuItem2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				propsMenuItem2_actionPerformed(e);
			}
		});
		logMenuItem.setMnemonic('L');
		logMenuItem.setText("View Log");
		logMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logMenuItem_actionPerformed(e);
			}
		});
		adminMenuItem.setMnemonic('D');
		adminMenuItem.setText("View Administration");
		adminMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adminMenuItem_actionPerformed(e);
			}
		});
		scheduleMenuItem.setMnemonic('H');
		scheduleMenuItem.setText("Schedule Harvest");
		scheduleMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scheduleMenuItem_actionPerformed(e);
			}
		});
		helpMenuItem.setMnemonic('E');
		helpMenuItem.setText("Help");
		helpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(112, 0,
				false));
		helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				helpMenuItem_actionPerformed(e);
			}
		});
		jMenu1.setMnemonic('O');
		jMenu1.setText("New Object");
		cplxMenuItem.setMnemonic('C');
		cplxMenuItem.setIcon(complexFolder);
		smplMenuItem.setIcon(simpleFolder);
		cplxMenuItem.setText("Complex Object");
		cplxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(88,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		cplxMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cplxMenuItem_actionPerformed(e);
			}
		});
		smplMenuItem.setMnemonic('S');
		smplMenuItem.setText("Simple Object");
		smplMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(83,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		smplMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				smplMenuItem_actionPerformed(e);
			}
		});
		propsMenuItem.setMnemonic('P');
		propsMenuItem.setText("Properties");
		propsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(80,
				java.awt.event.KeyEvent.CTRL_MASK, true));
		propsMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				propsMenuItem_actionPerformed(e);
			}
		});
		removeMenuItem.setMnemonic('R');
		removeMenuItem.setText("Remove");
		removeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(82,
				java.awt.event.KeyEvent.CTRL_MASK, false));
		removeMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeMenuItem_actionPerformed(e);
			}
		});
		removeAllMenuItem.setMnemonic('A');
		removeAllMenuItem.setText("Remove All");
		removeAllMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removeAllMenuItem_actionPerformed(e);
					}
				});
		folderButton.setToolTipText("Set Destination Folder");
		folderButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				folderButton_actionPerformed(e);
			}
		});
		jMenuBar1.add(fileMenu);
		jMenuBar1.add(toolsMenu);
		jMenuBar1.add(helpMenu);
		fileMenu.add(openMenuItem);
		fileMenu.add(removeMenuItem);
		fileMenu.add(removeAllMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);
		helpMenu.add(helpMenuItem);
		helpMenu.addSeparator();
		helpMenu.add(aboutMenuItem);
		toolsMenu.add(jMenu1);
		toolsMenu.addSeparator();
		toolsMenu.add(harvestMenuItem);
		toolsMenu.add(logMenuItem);
		toolsMenu.add(adminMenuItem);
		toolsMenu.add(scheduleMenuItem);
		toolsMenu.addSeparator();
		toolsMenu.add(propsMenuItem);
		root.add(configPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 2, 2, 2), 0, 0));
		root.add(filesPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						2, 2, 2, 2), 0, 0));
		filesPanel.add(jScrollPane1, new GridBagConstraints(0, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		jScrollPane1.getViewport().add(fileTree, null);
		root.add(toolBar, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(1, 2, 1, 2), 0, 0));
		root.add(statusBar, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(2, 1, 1, 1), 0, 0));
		this.getContentPane().add(root, null);
		configPanel.add(jPanel2, BorderLayout.CENTER);
		jPanel2.add(configLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 2, 5), 25, 0));
		jPanel2.add(destinationLabel, new GridBagConstraints(0, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(2, 5, 2, 5), 25, 0));
		jPanel2.add(configSelect, new GridBagConstraints(1, 0, 4, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 2, 5), 0, 0));
		jPanel2.add(profileLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						5, 2, 5), 25, 0));
		jPanel2.add(profileSelect, new GridBagConstraints(1, 2, 4, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(2, 5, 2, 5), 0, 0));
		jPanel2.add(userLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						5, 2, 5), 25, 0));
		jPanel2.add(userSelect, new GridBagConstraints(1, 3, 4, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(2, 5, 2, 5), 0, 0));
		jPanel2.add(destinationText, new GridBagConstraints(1, 1, 3, 1, 1.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(2, 5, 2, 5), 0, 0));
		/*
		 * jPanel2.add(addUser, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
		 * ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0,
		 * 0, 2), 0, 0)); jPanel2.add(removeUser, new GridBagConstraints(3, 2,
		 * 2, 1, 0.0, 0.0 ,GridBagConstraints.CENTER, GridBagConstraints.NONE,
		 * new Insets(0, 0, 0, 5), 0, 0));
		 */jPanel2.add(folderButton, new GridBagConstraints(4, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 2, 0, 5), 0, 0));
		newObjectMenu.add(complexMenuItem);
		newObjectMenu.add(simpleMenuItem);
		fileTreeMenu.addSeparator();
		fileTreeMenu.add(propsMenuItem2);
		jMenu1.add(cplxMenuItem);
		jMenu1.add(smplMenuItem);

		configSelect.setModel(configModel);
		configSelect.setSelectedIndex(0);
		userModel = new DefaultComboBoxModel(Config.getInstance().getUsers());
		userSelect.setModel(userModel);
		getCurrentProfile();
		Config.getInstance().addProfileListener(new ProfileListener() {
			public void profileAdded(Profile profile) {
				getCurrentProfile();
			}

			public void profileRemoved(Profile profile) {
				getCurrentProfile();
			}

			public void profileChanged(Profile profile) {
				getCurrentProfile();
			}
		});
		simpleMenuItem.setIcon(simpleFolder);
		complexMenuItem.setIcon(complexFolder);
		fileList.addTreeModelListener(new TreeModelListener() {
			public void treeStructureChanged(TreeModelEvent evt) {
				setButtonsBasedOnSelection();
			}

			public void treeNodesRemoved(TreeModelEvent evt) {
				setButtonsBasedOnSelection();
			}

			public void treeNodesInserted(TreeModelEvent evt) {
				setButtonsBasedOnSelection();
			}

			public void treeNodesChanged(TreeModelEvent evt) {
				setButtonsBasedOnSelection();
			}
		});
		fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				setButtonsBasedOnSelection();
			}
		});
		fileTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					// may need to decide if the current selection warrants this
					// or what the enable/disable state was
					TreePath path = fileTree.getPathForLocation(e.getX(), e
							.getY());
					if (path != null) {
						fileTree.setSelectionPath(path);
						fileTreeMenu.show(fileTree, e.getX(), e.getY());
					}
				}
			}
		});
		destinationText.getDocument().addDocumentListener(
				new DocumentListener() {
					public void changeProperty() {
						Configuration config = (Configuration) configSelect
								.getSelectedItem();
						config.setOutputDirectory(destinationText.getText());
					}

					public void changedUpdate(DocumentEvent evt) {
						changeProperty();
					}

					public void removeUpdate(DocumentEvent evt) {
						changeProperty();
					}

					public void insertUpdate(DocumentEvent evt) {
						changeProperty();
					}
				});

		MouseAdapter borderControl = new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				JButton source = (JButton) e.getSource();
				if (source.isEnabled()) {
					source.setBackground(over);
				}
			}

			public void mouseExited(MouseEvent e) {
				JButton source = (JButton) e.getSource();
				source.setBackground(normal);
			}
		};
		// addUser.addMouseListener(borderControl);
		// removeUser.addMouseListener(borderControl);
		// folderButton.addMouseListener(borderControl);
		userSelect.setSelectedItem(Config.getInstance().getDefaultUser());
		userSelect.setEditable(false);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (!busy) {
					exit();
				}
			}
		});

		this.setGlassPane(new GlassPane());
		setButtonsBasedOnSelection();
	}

	private void setButtonsBasedOnSelection() {
		int itemsSelected = fileTree.getSelectionCount();
		int itemsInRoot = ((RootNode) fileList.getRoot()).getChildCount();
		boolean someFiles = (itemsInRoot > 0)
				&& FileModel.hasAFile((BaseNode) fileList.getRoot());
		TreePath selected = fileTree.getSelectionPath();
		boolean canAddFiles = (itemsSelected == 1)
				&& (selected.getLastPathComponent() instanceof ObjectFolderNode);
		boolean userSelected = (userSelect.getSelectedItem() != null)
				&& (!userSelect.getSelectedItem().toString().trim().equals(""));
		boolean canProcess = (userSelected) && (someFiles);
		boolean canRemove = itemsSelected > 0;
		boolean canRemoveAll = itemsInRoot > 0;
		boolean canCreateNewObject = true;
		boolean canShowProperties = (itemsSelected == 1)
				&& (selected.getLastPathComponent() instanceof PropertyContainer);

		// buttons...
		propsMenuItem2.setEnabled(canShowProperties);

		// menu items...
		openMenuItem.setEnabled(canAddFiles);
		removeAllMenuItem.setEnabled(canRemoveAll);
		removeMenuItem.setEnabled(canRemove);
		smplMenuItem.setEnabled(canCreateNewObject);
		cplxMenuItem.setEnabled(canCreateNewObject);
		harvestMenuItem.setEnabled(canProcess);
		propsMenuItem.setEnabled(canShowProperties);

		// TOOLBAR
		toolBar.setCreateEnabled(canCreateNewObject);
		toolBar.setAddEnabled(canAddFiles);
		toolBar.setRemoveEnabled(canRemove);
		toolBar.setRemoveAllEnabled(canRemoveAll);
		toolBar.setPropertiesEnabled(canShowProperties);
		toolBar.setProcessEnabled(canProcess);

	}

	private void setDestination(File file) {
		destinationText.setText(file.getPath());
	}

	private void setDestination() {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Selecting destination folder");
		FileDialogUser listener = new FileDialogUser() {
			public void error(Throwable t) {
				Main.this.error(t);
			}

			public void saveTo(File file) {
			}

			public void openFile(File[] file, FileDialog from) {
				try {
					if ((file != null) && (file.length == 1)
							&& (file[0].isDirectory())) {
						Main.this.setDestination(file[0]);
					} else {
						LogManager
								.getInstance()
								.logMessage(LogMessage.ERROR,
										"Error selecting a SINGLE FOLDER for the output destination");
					}
				} catch (Exception ex) {
					error(ex);
				}
			}
		};

		showFileDialog("Select Folder", listener, false, false, false);
	}

	private void showFileDialog(String title, FileDialogUser controller,
			boolean save, boolean recurseFolders, boolean flattenFolders) {
		// create and open a fileDialog
		JDialog fileDialog = new JDialog(this, title, true);
		FileDialog filePanel = new FileDialog(this, controller, save,
				recurseFolders, flattenFolders);
		fileDialog.getContentPane().add(filePanel);
		fileDialog.pack();

		// set the location.
		Point p1 = this.getLocation();
		Dimension d1 = this.getSize();
		Dimension d2 = fileDialog.getSize();
		fileDialog.setLocation((p1.x + (d1.width / 2)) - (d2.width / 2),
				(p1.y + (d1.height / 2)) - (d2.height / 2));

		filePanel.setCurrentDirectory(Config.getInstance().getBaseHarvestDir());
		fileDialog.show();
	}

	private Point getVisibleLocation(Component parent, Dimension thing) {
		int bestx = 0;
		int besty = parent.getHeight();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// you may need to flip it to the top
		if (thing.getHeight() + parent.getLocationOnScreen().y > screenSize
				.getHeight()) {
			besty = -(int) thing.getHeight();
		}

		// you may need to push it right...
		if (parent.getLocationOnScreen().x < 0) {
			// bump right
			bestx = -parent.getLocationOnScreen().x; // make it positive
		}

		// you may need to push it left...
		if (parent.getLocationOnScreen().x + thing.getWidth() > screenSize
				.getWidth()) {
			// bump left
			bestx = (int) (screenSize.getWidth() - (parent
					.getLocationOnScreen().x + thing.getWidth()));
		}

		return new Point(bestx, besty);
	}

	protected void showLogWindow() {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Showing Log Window");
		// first time?
		if (logWindow == null) {
			logWindow = new LogWindow(this, logModel);
			// set the location.

			// maximise...
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			logWindow.setSize(screenSize);
			logWindow.setLocation(0, 0);
		}
		logWindow.show();
	}

	protected void showAdminWindow() {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Showing Admin Window");
		// first time?
		if (adminWindow == null) {
			adminWindow = new AdminWindow(this, "Hey!!!");
			// set the location.

			// maximise...
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			adminWindow.setSize(640, 480);
			adminWindow.setLocation((screenSize.width - 640) / 2,
					(screenSize.height - 480) / 2);
		}
		adminWindow.show();
	}

	protected void showScheduleWindow() {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Showing Schedule Window");
		// first time?
		if (scheduleWindow == null) {
			scheduleWindow = new ScheduleWindow(this, "Hey!!!");
		}
		scheduleWindow.show();
	}

	private void showAbout() {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Showing about");
		if (about == null) {
			about = new About(this);
		}

		// set the location.
		Point p1 = this.getLocation();
		Dimension d1 = this.getSize();
		Dimension d2 = about.getSize();
		about.setLocation((p1.x + (d1.width / 2)) - (d2.width / 2),
				(p1.y + (d1.height / 2)) - (d2.height / 2));

		about.show();
	}

	protected void showHelp() {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Showing help");
		Runnable showHelpThread = new Runnable() {
			public void run() {
				// first time?
				if (helpWindow == null) {
					helpWindow = new HelpWindow(Main.this);
					// set the location.

					// maximise...
					Dimension screenSize = Toolkit.getDefaultToolkit()
							.getScreenSize();
					helpWindow.setSize(screenSize);
					helpWindow.setLocation(0, 0);
				}

				helpWindow.show();
			}
		};
		Thread thread = new Thread(showHelpThread);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	protected void error(Throwable error) {
		LogManager.getInstance().logMessage(error);
	}

	/**
	 * This is the call from the file dialog with a selection of files for
	 * opening...
	 * 
	 * @param files The list of files to open.
	 * @param currentDirectory
	 * @throws IOException
	 */
	protected void openFile(File[] files, String currentDirectory,
			boolean recurseFolders, boolean flattenFolders, FolderNode into)
			throws IOException {
		FileAdderProcess adder = new FileAdderProcess(this, fileList, into,
				files, recurseFolders, flattenFolders);

		Thread t = new Thread(adder);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
		// SwingUtilities.invokeLater();
		Config.getInstance().setBaseHarvestDir(currentDirectory);
		setButtonsBasedOnSelection();
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LogManager.getInstance().logMessage(e);
		}

		try {
			
			Configurator.install(true);

			// force a config load at this point - if it hasn't happend
			// already...
			Main main = new Main();
			Config.getInstance();
			LogManager.getInstance().addLog(main.logModel);
			LogManager.getInstance().addLog(
					new FileLogger(Config.getInstance().createLogFileName()));
			LogManager.getInstance().logMessage(
					LogMessage.WORTHLESS_CHATTER,
					"Starting Application: "
							+ Config.getInstance().getApplicationName());

			PrintStream stdoutanderr = null;
			try {
				stdoutanderr = new PrintStream(new FileOutputStream(
						"Output.log"));
				System.setErr(stdoutanderr);
				System.setOut(stdoutanderr);
			} catch (Exception e) {
				// Sigh. Couldn't open the file.
				LogManager.getInstance().logMessage(LogMessage.ERROR,
						"Couldn't redirect standard error");
				System.out.println("Redirect:  Unable to open output file!");
				// System.exit (1);
			}

			main.jbInit();

			main.setTitle(Config.getInstance().getApplicationName());
			main.pack();
			// Center the window
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			main.setSize(screenSize.width / 2, main.getHeight());
			Dimension frameSize = main.getSize();
			if (frameSize.height > screenSize.height) {
				frameSize.height = screenSize.height;
			}
			if (frameSize.width > screenSize.width) {
				frameSize.width = screenSize.width;
			}
			main.setLocation((screenSize.width - frameSize.width) / 2,
					(screenSize.height - frameSize.height) / 2);
			main.setVisible(true);
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(ex);
		}
	}

	protected void removeAllNodes() {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Removing ALL files");
		RootNode root = (RootNode) fileList.getRoot();
		root.clear();
		resetStatus();
	}

	protected void removeSelected() {
		TreePath[] selected = fileTree.getSelectionPaths();
		if (selected == null)
			return;
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Removing selected files");
		for (int i = 0; i < selected.length; i++) {
			removeNode((BaseNode) selected[i].getLastPathComponent());
		}
		resetStatus();

		// reselect the lowest path (and expand if possible)...
		for (int i = 0; i < selected.length; i++) {
			fileTree.expandPath(selected[i].getParentPath());
		}
	}

	private void removeNode(BaseNode node) {
		((FolderNode) node.getParent()).removeNode(node);
	}

	public void harvest() {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Harvesting");
		Configuration config = (Configuration) configSelect.getSelectedItem();
		Runnable runnable = new HarvestProcess(this, fileTree, config);
		Thread t = new Thread(runnable);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	protected void exit() {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Closing down Harvester");
		try {
			Config.getInstance().writeConfig();
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(ex);
		}
		LogManager.getInstance().close();
		if (stdoutanderr != null) {
			try {
				stdoutanderr.close();
			} catch (Exception e) {
			}
		}
		System.exit(0);
	}

	private void openFiles(BaseNode candidate) {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Adding files to :" + candidate.getName());
		// check if anything can be added to this folder...
		ObjectFolderNode into = null;
		boolean recurse = true;
		boolean flatten = false;
		if (candidate instanceof ObjectFolderNode) {
			into = (ObjectFolderNode) candidate;
			if (into.getType() == ObjectFolderNode.COMPLEX) {
				recurse = true;
				flatten = false;
			}
			if (into.getType() == ObjectFolderNode.SIMPLE) {
				recurse = true;
				flatten = true;
			}
		} else {
			return;
		}
		final ObjectFolderNode reallyInto = into;

		FileDialogUser listener = new FileDialogUser() {
			public void error(Throwable t) {
				Main.this.error(t);
			}

			public void saveTo(File file) {
			}

			public void openFile(File[] files, FileDialog from) {
				try {
					Main.this.openFile(files, from.getCurrentDirectory(), from
							.shouldRecurse(), from.shouldFlatten(), reallyInto);
				} catch (Exception ex) {
					error(ex);
				}
			}
		};

		showFileDialog("Select File/s", listener, false, recurse, flatten);
	}

	void exitMenuItem_actionPerformed(ActionEvent e) {
		exit();
	}

	void openMenuItem_actionPerformed(ActionEvent e) {
		add();
	}

	void aboutMenuItem_actionPerformed(ActionEvent e) {
		showAbout();
	}

	void exit_actionPerformed(ActionEvent e) {
		exit();
	}

	protected void add() {
		BaseNode selected = (BaseNode) fileTree.getSelectionPath()
				.getLastPathComponent();
		openFiles(selected);
		resetStatus();
	}

	void add_actionPerformed(ActionEvent e) {
		add();
	}

	void harvest_actionPerformed(ActionEvent e) {
		harvest();
	}

	void remove_actionPerformed(ActionEvent e) {
		removeSelected();
	}

	void harvestMenuItem_actionPerformed(ActionEvent e) {
		harvest();
	}

	private class ConfigModel extends DefaultComboBoxModel {

		public Config getConfig() {
			return (Config) this.getSelectedItem();
		}

		public int getSize() {
			return Config.getInstance().getAvailableConfigs().size();
		}

		public Object getElementAt(int i) {
			return Config.getInstance().getAvailableConfigs().get(i);
		}

	}

	protected void showPropertyEditor() {
		BaseNode selected = (BaseNode) fileTree.getSelectionPath()
				.getLastPathComponent();
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Starting property editor for :" + selected.getName());

		if (selected instanceof ObjectFolderNode) {
			ObjectFolderNode node = (ObjectFolderNode) selected;

			// make a window for the property editor
			JDialog propFrame = new JDialog(this, "Properties", true);
			PropertyEditorController editor = new PropertyEditorController(node);
			propFrame.getContentPane().add(editor);
			propFrame.pack();

			// set the location.
			Point p1 = this.getLocation();
			Dimension d1 = this.getSize();
			Dimension d2 = propFrame.getSize();
			propFrame.setLocation((p1.x + (d1.width / 2)) - (d2.width / 2),
					(p1.y + (d1.height / 2)) - (d2.height / 2));
			propFrame.show();
		}
	}

	void showConfig() {
		// putup the description
		Configuration config = (Configuration) configSelect.getSelectedItem();
		destinationText.setText(config.getOutputDirectory());
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Setting configuration to: " + config.getName());
	}

	protected void setStatus(HarvestStatus status) {
		statusBar.setStatus(status);
	}

	protected void resetStatus() {
		// reset all of the files to unprocessed again...
		BaseNode baseNode = (BaseNode) fileList.getRoot();
		fileList.suspendEvents(true);
		baseNode.setStatus(HarvestStatus.BLANK, null);
		fileList.suspendEvents(false);
		fileList.nodeStructureChanged(baseNode); // now that events are
													// enabled, fire one for all
													// changes

		setStatus(HarvestStatus.BLANK);
	}

	void profileSelect_itemStateChanged(ItemEvent e) {
		Object profile = profileSelect.getSelectedItem();
		if ((profile != null) && (profile instanceof Profile)
				&& (e.getStateChange() == ItemEvent.SELECTED)) {
			Profile prof = (Profile) profile;
			Config.getInstance().setCurrentProfile(prof);
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
					"Current profile set to :" + prof.getName());
		}
	}

	void userSelect_itemStateChanged(ItemEvent e) {
		Object user = userSelect.getSelectedItem();
		if ((user != null) && (user instanceof User)
				&& (e.getStateChange() == ItemEvent.SELECTED)) {
			User usr = (User) user;
			Config.getInstance().setDefaultUser(usr);
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
					"Current user set to :" + usr.getName());
		}
	}

	void configSelect_itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			showConfig();
			resetStatus();
		}
	}

	protected void beginWizard(ObjectFolderNode newNode) {
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
				"Starting new object wizard for :" + newNode.getName());
		CreateObjectWizard wizard = new CreateObjectWizard(this, newNode);
	}

	protected void addNewComplexObject() {
		resetStatus();
		ObjectFolderNode newNode = new ObjectFolderNode("new complex",
				ObjectFolderNode.COMPLEX);
		((RootNode) fileList.getRoot()).addNode(newNode);
		beginWizard(newNode);
	}

	protected void addNewSimpleObject() {
		resetStatus();
		ObjectFolderNode newNode = new ObjectFolderNode("new simple",
				ObjectFolderNode.SIMPLE);
		((RootNode) fileList.getRoot()).addNode(newNode);
		beginWizard(newNode);
	}

	void complexMenuItem_actionPerformed(ActionEvent e) {
		addNewComplexObject();
	}

	void simpleMenuItem_actionPerformed(ActionEvent e) {
		addNewSimpleObject();
	}

	void removeAll_actionPerformed(ActionEvent e) {
		removeAllNodes();
	}

	void propsMenuItem2_actionPerformed(ActionEvent e) {
		showPropertyEditor();
	}

	void logMenuItem_actionPerformed(ActionEvent e) {
		showLogWindow();
	}

	void adminMenuItem_actionPerformed(ActionEvent e) {
		showAdminWindow();
	}

	void scheduleMenuItem_actionPerformed(ActionEvent e) {
		showScheduleWindow();
	}

	void cplxMenuItem_actionPerformed(ActionEvent e) {
		addNewComplexObject();
	}

	void smplMenuItem_actionPerformed(ActionEvent e) {
		addNewSimpleObject();
	}

	void propsMenuItem_actionPerformed(ActionEvent e) {
		showPropertyEditor();
	}

	void helpMenuItem_actionPerformed(ActionEvent e) {
		showHelp();
	}

	void getCurrentProfile() {
		profileModel = new DefaultComboBoxModel(Config.getInstance()
				.getAvailableProfiles().toArray());
		profileSelect.setModel(profileModel);
		profileSelect.setSelectedItem(Config.getInstance().getCurrentProfile());
	}

	protected void popupNewObjectMenu(Component c) {
		Point pos = getVisibleLocation(c, newObjectMenu.getSize());
		newObjectMenu.show(c, pos.x, pos.y);
	}

	public Object getProperty(String name, String condition) {
		// there are no conditions
		Object result = null;

		if (name.equalsIgnoreCase("user")) {
			Object user = userSelect.getSelectedItem();
			if ((user != null) && (user instanceof User)) {
				result = ((User) user).getName();
			}
		}

		if (result == null) {
			result = System.getProperty(name);
		}

		return result;
	}

	void folderButton_actionPerformed(ActionEvent e) {
		setDestination();
	}

	private class GlassPane extends JComponent {
		public GlassPane() {
			addKeyListener(new KeyAdapter() {
			});
			addMouseListener(new MouseAdapter() {
			});
		}
	}

	private boolean busy = false;

	public void setBusy(boolean busy) {
		this.busy = busy;

		if (busy) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			getGlassPane().setVisible(true);
		} else {
			setCursor(Cursor.getDefaultCursor());
			getGlassPane().setVisible(false);
		}

	}

	void removeAllMenuItem_actionPerformed(ActionEvent e) {
		removeAllNodes();
	}

	void removeMenuItem_actionPerformed(ActionEvent e) {
		removeSelected();
	}

}