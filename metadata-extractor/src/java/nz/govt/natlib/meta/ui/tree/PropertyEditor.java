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

package nz.govt.natlib.meta.ui.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import nz.govt.natlib.meta.ui.NLNZCombo;

/**
 * @author unascribed
 * @version 1.0
 */

public class PropertyEditor extends JPanel {

	PropertyContainer properties;

	HashMap oldProps;

	HashMap propEditors;

	GridBagLayout gridBagLayout2 = new GridBagLayout();

	public PropertyEditor(PropertyContainer properties) {
		this.properties = properties;

		// pull out original values.
		oldProps = new HashMap();
		propEditors = new HashMap();

		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setLayout(gridBagLayout2);

		// now layout the editors for editing the properties...
		Property[] props = properties.getProperties();
		for (int j = 0, i = 0; j < props.length; j++) {
			Object val = properties.getPropertyValue(props[j]);
			Property prop = props[j];
			oldProps.put(prop, val);
			if (prop.isVisible()) {
				Component comp = createComponentFor(properties, prop, val);
				propEditors.put(prop, comp);
				JLabel label = new JLabel(prop.getLabel());
				label.setLabelFor(comp);
				add(label, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(2, 5, 2, 5), 0, 0));
				add(comp, new GridBagConstraints(1, i, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(2, 5, 2, 5), 250, 0));
				i++;
			}
		}
		add(new JLabel(""), new GridBagConstraints(0, props.length, 2, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(2, 5, 2, 5), 0, 0));
	}

	protected void notityChange(Property prop) {
		// this changing may have affected others?
		Iterator it = propEditors.keySet().iterator();
		while (it.hasNext()) {
			Property p = (Property) it.next();
			if (p != prop) {
				PropertyEditorComp comp = (PropertyEditorComp) propEditors
						.get(p);
				comp.setValue(properties.getPropertyValue(p));
			}
		}
	}

	private Component createComponentFor(PropertyContainer container,
			Property prop, Object currentValue) {
		Component comp = null;
		if ((prop.getType() == Property.INTEGER)
				|| (prop.getType() == Property.STRING)) {
			TextPropertyEditor field = new TextPropertyEditor(prop, container);
			field.setMinimumSize(new Dimension(123, 21));
			field.setPreferredSize(new Dimension(123, 21));
			comp = field;
		}

		if (prop.getType() == Property.BOOLEAN) {
			BooleanPropertyEditor field = new BooleanPropertyEditor(prop,
					container);
			field.setMinimumSize(new Dimension(123, 21));
			field.setPreferredSize(new Dimension(123, 21));
			comp = field;
		}

		if (prop.getType() == Property.ENUMERATION) {
			EnumerationPropertyEditor field = new EnumerationPropertyEditor(
					prop, container);
			field.setMinimumSize(new Dimension(123, 21));
			field.setPreferredSize(new Dimension(123, 21));
			comp = field;
		}

		((PropertyEditorComp) comp).setValue(currentValue);
		comp.setEnabled(prop.isEditable());
		return comp;
	}

	private interface PropertyEditorComp {
		public void setValue(Object value);
	}

	private class BooleanPropertyEditor extends JCheckBox implements
			ActionListener, PropertyEditorComp {
		private Property property;

		private PropertyContainer container;

		public BooleanPropertyEditor(Property property,
				PropertyContainer container) {
			this.property = property;
			this.container = container;
			this.setText("");
		}

		public void setValue(Object value) {
			boolean val = value == Boolean.TRUE;
			if (val != isSelected()) {
				setSelected(val);
			}
		}

		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			if (enabled) {
				this.addActionListener(this);
			} else {
				this.removeActionListener(this);
			}
		}

		private void changeProperty() {
			container.setPropertyValue(property,
					this.isSelected() ? Boolean.TRUE : Boolean.FALSE);
			notityChange(property);
		}

		public void actionPerformed(ActionEvent evt) {
			changeProperty();
		}
	}

	private class EnumerationPropertyEditor extends NLNZCombo implements
			ItemListener, PropertyEditorComp {
		Property property;

		PropertyContainer container;

		public EnumerationPropertyEditor(Property property,
				PropertyContainer container) {
			this.property = property;
			this.container = container;

			Iterator it = property.getAllowedValues();
			Vector v = new Vector();
			while (it.hasNext()) {
				v.add(it.next());
			}
			setModel(new DefaultComboBoxModel(v));

		}

		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			if (enabled) {
				this.addItemListener(this);
			} else {
				this.removeItemListener(this);
			}
		}

		public void setValue(Object value) {
			if (!value.equals(getSelectedItem())) {
				setSelectedItem(value);
			}
		}

		private void changeProperty() {
			container.setPropertyValue(property, getSelectedItem());
			notityChange(property);
		}

		public void itemStateChanged(ItemEvent e) {
			changeProperty();
		}
	}

	private class TextPropertyEditor extends JTextField implements
			DocumentListener, PropertyEditorComp {

		Property property;

		PropertyContainer container;

		public TextPropertyEditor(Property property, PropertyContainer container) {
			this.property = property;
			this.container = container;
		}

		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			if (enabled) {
				this.getDocument().addDocumentListener(this);
			} else {
				this.getDocument().removeDocumentListener(this);
			}
		}

		private void changeProperty() {
			if (property.isValid(getText())) {
				setForeground(Color.black);
			} else {
				setForeground(Color.red);
			}
			container.setPropertyValue(property, getText());
			notityChange(property);
		}

		public void setValue(Object value) {
			if (!value.equals(getText())) {
				setText(value.toString());
			}
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
	}

	public void closeWindow() {
		this.getTopLevelAncestor().setVisible(false);
	}

	public void ok() {
		// just close and leave
		closeWindow();
	}

	public void cancel() {
		// change the properties back the their original values...
		Iterator it = oldProps.keySet().iterator();
		while (it.hasNext()) {
			Property prop = (Property) it.next();
			Object val = oldProps.get(prop);
			properties.setPropertyValue(prop, val);
		}
	}
}