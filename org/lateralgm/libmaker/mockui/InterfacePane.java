/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.mockui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.backend.Action;
import org.lateralgm.libmaker.backend.Action.InterfaceKind;
import org.lateralgm.libmaker.backend.Argument;
import org.lateralgm.libmaker.components.EnumRenderer;
import org.lateralgm.libmaker.mockui.MockUI.ActionPanel;
import org.lateralgm.libmaker.mockui.MockUI.GroupPanel;

public class InterfacePane extends GroupPanel implements ActionPanel,ChangeListener
	{
	private static final long serialVersionUID = 1L;

	JComboBox dKind;
	JCheckBox cbQuestion, cbApply, cbRelative;

	SpinnerNumberModel smArgNum;
	JTextField tArgNames[];
	JComboBox dArgTypes[];
	JTextField tArgVals[];
	JTextField tArgOpts[];

	protected void initKeyComponents()
		{
		dKind = new JComboBox(InterfaceKind.values());
		dKind.setRenderer(new EnumRenderer("ActionIfaceKind.")); //$NON-NLS-1$

		cbQuestion = new JCheckBox(Messages.getString("InterfacePane.QUESTION")); //$NON-NLS-1$
		cbApply = new JCheckBox(Messages.getString("InterfacePane.APPLY")); //$NON-NLS-1$
		cbRelative = new JCheckBox(Messages.getString("InterfacePane.RELATIVE")); //$NON-NLS-1$

		smArgNum = new SpinnerNumberModel(6,0,Action.MAX_ARGS,1); //so all 6 arguments are initially visible
		smArgNum.addChangeListener(this);

		tArgNames = new JTextField[Action.MAX_ARGS];
		dArgTypes = new JComboBox[Action.MAX_ARGS];
		tArgVals = new JTextField[Action.MAX_ARGS];
		tArgOpts = new JTextField[Action.MAX_ARGS];

		for (int arg = 0; arg < Action.MAX_ARGS; arg++)
			{
			dArgTypes[arg] = new JComboBox(Argument.Kind.values());
			dArgTypes[arg].setSelectedItem(Argument.Kind.MENU); //so the menu text field is initially visible
			dArgTypes[arg].setRenderer(new EnumRenderer("ArgumentType.")); //$NON-NLS-1$

			tArgNames[arg] = new JTextField(9);
			tArgVals[arg] = new JTextField(8);
			tArgOpts[arg] = new JTextField(5);

			final JTextField tF = tArgOpts[arg];
			dArgTypes[arg].addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
						{
						JComboBox cb = (JComboBox) e.getSource();
						boolean b = cb.isVisible() && cb.getSelectedItem() == Argument.Kind.MENU;
						tF.setVisible(b);
						updateUI(); //inform parent that a component needs drawing
						}
				});
			}
		}

	@Override
	protected void layoutComponents(GroupLayout layout)
		{
		initKeyComponents();

		JLabel lKind = new JLabel(Messages.getString("InterfacePane.KIND")); //$NON-NLS-1$
		JLabel lArgNum = new JLabel(Messages.getString("InterfacePane.ARG_COUNT")); //$NON-NLS-1$

		JSpinner sArgNum = new JSpinner(smArgNum);
		JComponent editor = sArgNum.getEditor();
		if (editor instanceof JSpinner.DefaultEditor)
			{
			JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
			tf.setColumns(3);
			tf.setHorizontalAlignment(JTextField.LEFT);
			}

		ParallelGroup hgC1 = layout.createParallelGroup();
		ParallelGroup hgC2 = layout.createParallelGroup();
		ParallelGroup hgC3 = layout.createParallelGroup();
		ParallelGroup hgC4 = layout.createParallelGroup();
		SequentialGroup vGroup = layout.createSequentialGroup();

		int PREF = GroupLayout.PREFERRED_SIZE, DEF = GroupLayout.DEFAULT_SIZE;

		for (int arg = 0; arg < Action.MAX_ARGS; arg++)
			{
			hgC1.addComponent(tArgNames[arg],PREF,DEF,PREF);
			hgC2.addComponent(dArgTypes[arg],PREF,DEF,PREF);
			hgC3.addComponent(tArgVals[arg],PREF,DEF,PREF);
			hgC4.addComponent(tArgOpts[arg],PREF,DEF,Short.MAX_VALUE);

			vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			/**/.addComponent(tArgNames[arg])
			/**/.addComponent(dArgTypes[arg])
			/**/.addComponent(tArgVals[arg])
			/**/.addComponent(tArgOpts[arg]));
			}

		layout.setHorizontalGroup(layout.createSequentialGroup()
		/**/.addGroup(layout.createParallelGroup()
		/*	*/.addGroup(layout.createSequentialGroup()
		/*		*/.addComponent(lKind)
		/*		*/.addComponent(dKind,PREF,DEF,PREF))
		/*	*/.addComponent(cbQuestion)
		/*	*/.addComponent(cbApply)
		/*	*/.addComponent(cbRelative))
		/**/.addGap(25)
		/**/.addGroup(layout.createParallelGroup()
		/*	*/.addGroup(layout.createSequentialGroup()
		/*		*/.addComponent(lArgNum)
		/*		*/.addComponent(sArgNum,PREF,DEF,PREF))
		/*	*/.addGroup(layout.createSequentialGroup()
		/*		*/.addGroup(hgC1)
		/*		*/.addGroup(hgC2)
		/*		*/.addGroup(hgC3)
		/*		*/.addGroup(hgC4)))
		/**/.addContainerGap(0,Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
		/**/.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		/*	*/.addComponent(lKind)
		/*	*/.addComponent(dKind,PREF,DEF,PREF)
		/*	*/.addComponent(lArgNum)
		/*	*/.addComponent(sArgNum,PREF,DEF,PREF))
		/**/.addGroup(layout.createParallelGroup()
		/*	*/.addGroup(layout.createSequentialGroup()
		/*		*/.addComponent(cbQuestion)
		/*		*/.addComponent(cbApply)
		/*		*/.addComponent(cbRelative))
		/*	*/.addGroup(vGroup)));
		}

	@Override
	public void setComponents(Action a)
		{
		dKind.setSelectedItem(a.ifaceKind);
		cbQuestion.setSelected(a.question);
		cbApply.setSelected(a.apply);
		cbRelative.setSelected(a.relative);

		smArgNum.setValue(a.argNum);
		for (int arg = 0; arg < Action.MAX_ARGS; arg++)
			{
			Argument aa = a.arguments[arg];
			tArgNames[arg].setText(aa.caption);
			dArgTypes[arg].setSelectedItem(aa.kind);
			tArgVals[arg].setText(aa.defValue);
			tArgOpts[arg].setText(aa.menuOptions);
			}
		}

	@Override
	public void stateChanged(ChangeEvent e)
		{
		int args = smArgNum.getNumber().intValue();
		for (int arg = 0; arg < Action.MAX_ARGS; arg++)
			{
			boolean visible = arg < args;
			tArgNames[arg].setVisible(visible);
			dArgTypes[arg].setVisible(visible);
			tArgVals[arg].setVisible(visible);
			tArgOpts[arg].setVisible(visible && dArgTypes[arg].getSelectedItem() == Argument.Kind.MENU);
			}
		}
	}
