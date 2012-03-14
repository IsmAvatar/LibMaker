/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * 
 * LibMaker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LibMaker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License (COPYING) for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.lateralgm.libmaker.mockui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.backend.Action;
import org.lateralgm.libmaker.backend.Action.PAction;
import org.lateralgm.libmaker.backend.Library;
import org.lateralgm.libmaker.backend.Library.PLibrary;
import org.lateralgm.libmaker.backend.PropertyMap.PropertyListener;
import org.lateralgm.libmaker.backend.PropertyMap.PropertyUpdateEvent;
import org.lateralgm.libmaker.components.ListListModel;
import org.lateralgm.libmaker.components.NumberField;
import org.lateralgm.libmaker.components.ObservableList.ListUpdateEvent;
import org.lateralgm.libmaker.components.ObservableList.ListUpdateListener;
import org.lateralgm.libmaker.components.SingleListSelectionModel;
import org.lateralgm.libmaker.uilink.PropertyLink.PLFactory;

public class MockUI extends JSplitPane implements ListSelectionListener,PropertyListener<PAction>,
		ListUpdateListener
	{
	private static final long serialVersionUID = 1L;

	Library lib;
	//The list is pulled up because we need it to control the other panels
	ListListModel<Action> mActions;
	JList lActions;
	ControlPane control;
	GeneralPane general;
	InterfacePane iface;

	public MockUI()
		{
		super();
		setLeftComponent(control = new ControlPane());
		setRightComponent(makeMainPane());
		}

	private static JButton makeButton(String key, ActionListener listener)
		{
		JButton but = new JButton(Messages.getString(key),Messages.getIconForKey(key));
		but.addActionListener(listener);
		return but;
		}

	class ControlPane extends GroupPanel implements ActionListener
		{
		private static final long serialVersionUID = 1L;

		JTextField tCaption;
		NumberField tId;
		JButton bCycle, bInfo, bCode;
		JButton bAdd, bDel, bUp, bDown;
		JCheckBox cbAdvanced;

		Library lib;
		PLFactory<PLibrary> plf;

		protected void initKeyComponents()
			{
			plf = new PLFactory<PLibrary>();

			tCaption = new JTextField();
			plf.make(tCaption,PLibrary.CAPTION);
			tId = new NumberField(0,1000000);
			plf.make(tId,PLibrary.ID);
			cbAdvanced = new JCheckBox(Messages.getString("MockUI.ADVANCED")); //$NON-NLS-1$
			plf.make(cbAdvanced,PLibrary.ADVANCED);

			lActions = new JList(mActions = new ListListModel<Action>());
			lActions.setCellRenderer(new DefaultListCellRenderer()
				{
					private static final long serialVersionUID = 1L;

					public Component getListCellRendererComponent(JList list, Object value, int index,
							boolean isSelected, boolean cellHasFocus)
						{
						if (value instanceof Action) value = ((Action) value).get(PAction.NAME);
						return super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
						}
				});
			//Prototype only used for size calculation, so it doesn't need translating
			lActions.setPrototypeCellValue("Sample Action Cell"); //$NON-NLS-1$
			lActions.setSelectionModel(new SingleListSelectionModel());
			lActions.addListSelectionListener(MockUI.this);

			bCycle = new JButton(Messages.getIconForKey("MockUI.CYCLE")); //$NON-NLS-1$
			bCycle.setToolTipText(Messages.getString("MockUI.CYCLE_TIP")); //$NON-NLS-1$
			bCycle.addActionListener(this);
			String key = "MockUI.INFO"; //$NON-NLS-1$
			bInfo = new JButton(Messages.getString(key),Messages.getIconForKey(key));
			bInfo.addActionListener(this);
			key = "MockUI.CODE"; //$NON-NLS-1$
			bCode = new JButton(Messages.getString(key),Messages.getIconForKey(key));
			bCode.addActionListener(this);

			bAdd = makeButton("MockUI.ADD",this); //$NON-NLS-1$
			bDel = makeButton("MockUI.DELETE",this); //$NON-NLS-1$
			bUp = makeButton("MockUI.UP",this); //$NON-NLS-1$
			bDown = makeButton("MockUI.DOWN",this); //$NON-NLS-1$
			}

		@Override
		protected void layoutComponents(GroupLayout layout)
			{
			initKeyComponents();

			JLabel lCaption = new JLabel(Messages.getString("MockUI.CAPTION")); //$NON-NLS-1$
			JLabel lId = new JLabel(Messages.getString("MockUI.LIB_ID")); //$NON-NLS-1$
			JLabel labActions = new JLabel(Messages.getString("MockUI.ACTIONS")); //$NON-NLS-1$
			JScrollPane listScroll = new JScrollPane(lActions);

			JPanel listButtons = new JPanel(new GridLayout(2,2));
			listButtons.add(bAdd);
			listButtons.add(bUp);
			listButtons.add(bDel);
			listButtons.add(bDown);

			int PREF = GroupLayout.PREFERRED_SIZE, DEF = GroupLayout.DEFAULT_SIZE;

			layout.setHorizontalGroup(layout.createParallelGroup()
			/**/.addComponent(lCaption)
			/**/.addComponent(tCaption)
			/**/.addComponent(lId)
			/**/.addGroup(layout.createSequentialGroup()
			/*	*/.addComponent(tId)
			/*	*/.addComponent(bCycle,16,16,16))
			/**/.addComponent(bInfo,PREF,DEF,Short.MAX_VALUE)
			/**/.addComponent(bCode,PREF,DEF,Short.MAX_VALUE)
			/**/.addComponent(cbAdvanced)
			/**/.addComponent(labActions)
			/**/.addComponent(listScroll,0,0,Short.MAX_VALUE)
			/**/.addComponent(listButtons));

			layout.setVerticalGroup(layout.createSequentialGroup()
			/**/.addComponent(lCaption)
			/**/.addComponent(tCaption,PREF,DEF,PREF)
			/**/.addComponent(lId)
			/**/.addGroup(layout.createParallelGroup()
			/*	*/.addComponent(tId,PREF,DEF,PREF)
			/*	*/.addComponent(bCycle,16,16,16))
			/**/.addComponent(bInfo)
			/**/.addComponent(bCode)
			/**/.addComponent(cbAdvanced)
			/**/.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			/**/.addComponent(labActions)
			/**/.addComponent(listScroll)
			/**/.addComponent(listButtons,PREF,DEF,PREF));
			}

		public void setLibrary(Library lib)
			{
			this.lib = lib;
			plf.setMap(lib.properties);
			}

		@Override
		public void actionPerformed(ActionEvent e)
			{
			Object s = e.getSource();
			if (s == bCycle)
				{
				tId.setValue(Library.randomId());
				return;
				}
			if (s == bInfo)
				{
				InfoPane.showInDialog(null,lib);
				return;
				}
			if (s == bCode)
				{
				//TODO: Initialization Code
				return;
				}
			if (s == bAdd)
				{
				Action a = new Action();
				lib.actions.add(a);
				lActions.setSelectedValue(a,true);
				}
			if (s == bDel)
				{
				int ind = lActions.getSelectedIndex();
				if (ind == -1) return;
				lib.actions.remove(ind);
				if (ind >= lib.actions.size()) ind = lib.actions.size() - 1;
				lActions.setSelectedIndex(ind);
				}
			if (s == bUp)
				{
				int ind = lActions.getSelectedIndex();
				if (ind < 1) return;
				Action a1 = lib.actions.get(ind - 1);
				Action a2 = lib.actions.get(ind);
				lib.actions.set(ind - 1,a2);
				lib.actions.set(ind,a1);
				lActions.setSelectedIndex(ind - 1);
				}
			if (s == bDown)
				{
				int ind = lActions.getSelectedIndex();
				if (ind == -1 || ind > lib.actions.size() - 2) return;
				Action a1 = lib.actions.get(ind);
				Action a2 = lib.actions.get(ind + 1);
				lib.actions.set(ind,a2);
				lib.actions.set(ind + 1,a1);
				lActions.setSelectedIndex(ind + 1);
				}
			lActions.updateUI();
			}
		}

	JPanel makeMainPane()
		{
		general = new GeneralPane();
		iface = new InterfacePane();

		general.setBorder(BorderFactory.createTitledBorder(Messages.getString("MockUI.GENERAL"))); //$NON-NLS-1$
		iface.setBorder(BorderFactory.createTitledBorder(Messages.getString("MockUI.INTERFACE"))); //$NON-NLS-1$

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));

		p.add(general);
		p.add(iface);

		return p;
		}

	public void setLibrary(Library lib)
		{
		this.lib = lib;
		control.setLibrary(lib);
		mActions.setList(lib.actions);
		lActions.setSelectedIndex(lib.actions.isEmpty() ? -1 : 0);
		lib.actions.addListUpdateListener(this);
		for (Action a : lib.actions)
			a.properties.addPropertyListener(PAction.NAME,this);
		lActions.updateUI();
		}

	public static interface ActionPanel
		{
		void setComponents(Action a);
		}

	public static abstract class GroupPanel extends JPanel
		{
		private static final long serialVersionUID = 1L;

		public GroupPanel()
			{
			super();

			GroupLayout layout = new GroupLayout(this);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			setLayout(layout);

			layoutComponents(layout);
			}

		protected abstract void layoutComponents(GroupLayout layout);
		}

	boolean wasSelection = true; //initially true so a change registers the first time

	@Override
	public void valueChanged(ListSelectionEvent e)
		{
		Object v = lActions.getSelectedValue();
		boolean hasSelection = (v != null && v instanceof Action);
		if (hasSelection != wasSelection)
			{
			wasSelection = hasSelection;

			control.bDel.setEnabled(hasSelection);
			control.bUp.setEnabled(hasSelection);
			control.bDown.setEnabled(hasSelection);

			general.setVisible(hasSelection);
			iface.setVisible(hasSelection);
			}
		if (!hasSelection) return;

		int ind = lActions.getSelectedIndex();
		control.bUp.setEnabled(ind != 0);
		control.bDown.setEnabled(ind != lActions.getModel().getSize() - 1);

		Action a = (Action) v;
		general.setComponents(a);
		iface.setComponents(a);
		}

	@Override
	public void propertyUpdate(PropertyUpdateEvent<PAction> e)
		{
		lActions.updateUI();
		}

	@Override
	public void listUpdate(ListUpdateEvent evt)
		{
		if (evt.type != ListUpdateEvent.Type.ADDED) return;
		for (int i = evt.fromIndex; i <= evt.toIndex; i++)
			lib.actions.get(i).properties.addPropertyListener(PAction.NAME,this);
		lActions.updateUI();
		}
	}
