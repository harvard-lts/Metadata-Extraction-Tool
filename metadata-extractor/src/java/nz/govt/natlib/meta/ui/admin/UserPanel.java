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

/*
 * Created on 21/04/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.govt.natlib.meta.ui.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.User;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.meta.ui.ImageButton;
import nz.govt.natlib.meta.ui.ImagePanel;

/**
 * @author AParker
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UserPanel extends JPanel {

	private JList userList;

	private ImageButton addUser, delUser;

	private User[] users;

	private JFrame parent;

	private ListModel listModel;

	private ImageIcon addPic, userPic;

	private ImageIcon delPic, cancelPic;

	public UserPanel(JFrame parent) {
		this.parent = parent;
		this.setLayout(new BorderLayout());
		users = Config.getEditInstance().getUsers();
		userList = new JList(Config.getEditInstance().getUsers());
		listModel = userList.getModel();
		jbInit();
	}

	public void refresh() {
		users = Config.getEditInstance().getUsers();
		DefaultListModel listModel = new DefaultListModel();
		for (int i = 0; i < users.length; i++) {
			listModel.addElement(users[i]);
		}
		userList.setModel(listModel);
	}

	private void jbInit() {
		JPanel mainPnl = new JPanel();
		Border userBorder = new CompoundBorder(new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(148, 145, 140)),
				"Add/Remove Users"), new EmptyBorder(8, 8, 8, 8));
		JScrollPane scroll = new JScrollPane(userList);
		JPanel bufferPnl = new JPanel();
		JPanel buttonPnl = new JPanel(new GridLayout(1, 2, 5, 5));
		JPanel buttonBfrPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		mainPnl.setLayout(new BorderLayout());
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		try {
			addPic = new ImageIcon(ImagePanel.resolveImage("add_user.gif"));
			delPic = new ImageIcon(ImagePanel.resolveImage("delete_user.gif"));
			cancelPic = new ImageIcon(ImagePanel
					.resolveImage("button_cancel.gif"));
			userPic = new ImageIcon(ImagePanel.resolveImage("icon_user.gif"));
		} catch (Exception e) {
			LogManager.getInstance().logMessage(
					new LogMessage(LogMessage.ERROR, e, "Image not found",
							"Installation may be corrupt?"));
		}
		addUser = new ImageButton("Add", addPic);
		addUser.addActionListener(new AddUserAction());
		delUser = new ImageButton("Del", delPic);
		delUser.addActionListener(new DelUserAction());
		buttonPnl.add(addUser);
		buttonPnl.add(delUser);
		buttonBfrPnl.add(buttonPnl);
		mainPnl.add(scroll, BorderLayout.CENTER);
		mainPnl.add(buttonBfrPnl, BorderLayout.SOUTH);
		mainPnl.setBorder(userBorder);
		bufferPnl.add(mainPnl);
		this.add(bufferPnl, BorderLayout.CENTER);
		userList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				refreshButtons();
			}
		});
		if (users.length > 0) {
			userList.setSelectedIndex(0);
		}
	}

	private void refreshButtons() {
		delUser.setEnabled(userList.getSelectedIndex() > -1);
	}

	private DefaultListModel getNewDefaultModel() {
		DefaultListModel newListModel = new DefaultListModel();
		for (int i = 0; i < listModel.getSize(); i++) {
			newListModel.addElement(listModel.getElementAt(i));
		}
		return newListModel;
	}

	private void addNewUser() {
		NewUserDlg dlg = new NewUserDlg();
		if (dlg.showDialog()) {
			String userName = dlg.getUserName();
			if (userName.length() > 0) {
				User user = new User(userName);
				LogManager.getInstance().logMessage(LogMessage.INFO,
						"Adding user " + userName);
				DefaultListModel newListModel = getNewDefaultModel();
				if (!newListModel.contains(user)) {
					newListModel.addElement(user);
					userList.setModel(newListModel);
					listModel = newListModel;
					userList.setSelectedIndex(listModel.getSize() - 1);
					Config.getEditInstance().addUser(
							new User(dlg.getUserName()));
				} else {
					LogManager.getInstance().logMessage(LogMessage.INFO,
							"User " + userName + " already exists!");
					JOptionPane.showMessageDialog(parent,
							"User already exists", "User not added",
							JOptionPane.ERROR_MESSAGE, userPic);
				}
			}
		}
	}

	private void delUser() {
		int idx = userList.getSelectedIndex();
		User user = (User) userList.getSelectedValue();
		if (idx > -1) {
			if (JOptionPane.showConfirmDialog(this,
					"Are you sure you wish to delete " + user.getName(),
					"Remove User?", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, delPic) == JOptionPane.NO_OPTION) {
				return;
			}
			LogManager.getInstance().logMessage(LogMessage.INFO,
					"Removing user " + user.getName());
			Config.getEditInstance().removeUser(user);
			DefaultListModel newListModel = getNewDefaultModel();
			userList.setModel(newListModel);
			newListModel.removeElementAt(idx);
			int newSize = newListModel.getSize();
			if (newSize > 0) {
				userList.setSelectedIndex(Math.min(idx, newSize - 1));
			}
			listModel = newListModel;
			refreshButtons();
		}
	}

	public List getUsers() {
		ArrayList l = new ArrayList();
		for (int i = 0; i < listModel.getSize(); i++) {
			l.add(listModel.getElementAt(i));
		}
		return l;
	}

	private class AddUserAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			addNewUser();
		}
	}

	private class DelUserAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			delUser();
		}
	}

	private class NewUserDlg extends JDialog {
		private JTextField username = new JTextField();

		private ImageButton cancel = new ImageButton("Cancel", cancelPic);

		private ImageButton ok = new ImageButton("Add", addPic);

		private String newUserText;

		public NewUserDlg() {
			super(parent, true);
			JPanel topPnl = new JPanel(new BorderLayout(5, 5));
			getContentPane().setLayout(new BorderLayout(5, 5));
			getContentPane().add(topPnl, BorderLayout.NORTH);
			topPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			topPnl.add(username, BorderLayout.CENTER);
			topPnl.add(new JLabel("Name: "), BorderLayout.WEST);
			JPanel buttonPnl = new JPanel(new BorderLayout());
			buttonPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			JPanel buttons = new JPanel(new GridLayout(1, 2, 5, 5));
			buttonPnl.add(buttons, BorderLayout.EAST);
			buttons.add(cancel);
			buttons.add(ok);
			getContentPane().add(buttonPnl, BorderLayout.SOUTH);
			ActionListener okListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					newUserText = username.getText();
					setVisible(false);
				}
			};
			username.addActionListener(okListener);
			ok.addActionListener(okListener);
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			this.setTitle("Add New User");
			this.setSize(200, 86);
			this.setResizable(false);
			Dimension size = this.getSize();
			Dimension parentSize = parent.getSize();
			Point parentLoc = parent.getLocation();
			int x = ((parentSize.width - size.width) / 2) + parentLoc.x;
			int y = ((parentSize.height - size.height) / 2) + parentLoc.y;
			this.setLocation(x, y);

		}

		public boolean showDialog() {
			show();
			boolean res = (newUserText != null) && (newUserText.length() > 0);
			if (!res) {
				JOptionPane.showMessageDialog(parent, "No user entered",
						"User not added", JOptionPane.ERROR_MESSAGE, userPic);
			}
			return res;
		}

		public String getUserName() {
			return newUserText;
		}
	}
}
