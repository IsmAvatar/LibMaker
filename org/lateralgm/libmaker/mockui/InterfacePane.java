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
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.backend.Action;
import org.lateralgm.libmaker.backend.Action.InterfaceKind;
import org.lateralgm.libmaker.backend.Action.PAction;
import org.lateralgm.libmaker.backend.Argument;
import org.lateralgm.libmaker.backend.Argument.PArgument;
import org.lateralgm.libmaker.components.EnumRenderer;
import org.lateralgm.libmaker.mockui.MockUI.ActionPanel;
import org.lateralgm.libmaker.mockui.MockUI.GroupPanel;
import org.lateralgm.libmaker.uilink.PropertyLink.PLFactory;

public class InterfacePane extends GroupPanel implements ActionPanel,ChangeListener,ActionListener
	{
	private static final long serialVersionUID = 1L;

	PLFactory<PAction> plf;
	JComboBox<Integer> dKind;
	JCheckBox cbQuestion, cbApply, cbRelative;
	JLabel lArgNum; //so we can toggle its visibility
	JSpinner sArgNum;
	SpinnerNumberModel smArgNum;
	ArgumentInfo args[];

	class ArgumentInfo implements ActionListener
		{
		public JTextField tName, tVal, tOpts;
		public JComboBox<Integer> dType;
		PLFactory<PArgument> plf;

		public ArgumentInfo()
			{
			plf = new PLFactory<PArgument>();

			tName = new JTextField(9);
			tVal = new JTextField(8);
			tOpts = new JTextField(5);
			plf.make(tName,PArgument.CAPTION);
			plf.make(tVal,PArgument.DEF_VALUE);
			plf.make(tOpts,PArgument.MENU_OPTS);

			dType = new JComboBox(Argument.Kind.values());
			dType.setSelectedItem(Argument.Kind.MENU); //so the menu text field is initially visible
			dType.setRenderer(new EnumRenderer("ArgumentType.")); //$NON-NLS-1$
			dType.addActionListener(this);
			plf.make(dType,PArgument.KIND);
			}

		public void setComponents(Argument arg)
			{
			plf.setMap(arg.properties);
			}

		public void setVisible(boolean vis)
			{
			tName.setVisible(vis);
			dType.setVisible(vis);
			tVal.setVisible(vis);
			tOpts.setVisible(vis && dType.getSelectedItem() == Argument.Kind.MENU);
			}

		@Override
		public void actionPerformed(ActionEvent e)
			{
			@SuppressWarnings("unchecked")
			JComboBox<Integer> cb = (JComboBox<Integer>) e.getSource();
			boolean b = cb.isVisible() && cb.getSelectedItem() == Argument.Kind.MENU;
			tOpts.setVisible(b);
			updateUI(); //inform parent panel that a component needs drawing
			}
		}

	protected void initKeyComponents()
		{
		plf = new PLFactory<PAction>();

		dKind = new JComboBox(InterfaceKind.values());
		dKind.setRenderer(new EnumRenderer("ActionIfaceKind.")); //$NON-NLS-1$
		dKind.addActionListener(this);
		plf.make(dKind,PAction.IFACE_KIND);

		cbQuestion = new JCheckBox(Messages.getString("InterfacePane.QUESTION")); //$NON-NLS-1$
		cbApply = new JCheckBox(Messages.getString("InterfacePane.APPLY")); //$NON-NLS-1$
		cbRelative = new JCheckBox(Messages.getString("InterfacePane.RELATIVE")); //$NON-NLS-1$
		plf.make(cbQuestion,PAction.QUESTION);
		plf.make(cbApply,PAction.APPLY);
		plf.make(cbRelative,PAction.RELATIVE);

		lArgNum = new JLabel(Messages.getString("InterfacePane.ARG_COUNT")); //$NON-NLS-1$

		smArgNum = new SpinnerNumberModel(Action.MAX_ARGS,0,Action.MAX_ARGS,1); //so all arguments are initially visible
		smArgNum.addChangeListener(this);

		sArgNum = new JSpinner(smArgNum);
		JComponent editor = sArgNum.getEditor();
		if (editor instanceof JSpinner.DefaultEditor)
			{
			JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
			tf.setColumns(3);
			tf.setHorizontalAlignment(JTextField.LEFT);
			plf.make(tf,PAction.ARG_NUM);
			}

		args = new ArgumentInfo[Action.MAX_ARGS];
		for (int arg = 0; arg < Action.MAX_ARGS; arg++)
			args[arg] = new ArgumentInfo();
		}

	@Override
	protected void layoutComponents(GroupLayout layout)
		{
		initKeyComponents();

		JLabel lKind = new JLabel(Messages.getString("InterfacePane.KIND")); //$NON-NLS-1$

		ParallelGroup hgC1 = layout.createParallelGroup();
		ParallelGroup hgC2 = layout.createParallelGroup();
		ParallelGroup hgC3 = layout.createParallelGroup();
		ParallelGroup hgC4 = layout.createParallelGroup();
		SequentialGroup vGroup = layout.createSequentialGroup();

		int PREF = GroupLayout.PREFERRED_SIZE, DEF = GroupLayout.DEFAULT_SIZE;

		for (ArgumentInfo arg : args)
			{
			hgC1.addComponent(arg.tName,PREF,DEF,PREF);
			hgC2.addComponent(arg.dType,PREF,DEF,PREF);
			hgC3.addComponent(arg.tVal,PREF,DEF,PREF);
			hgC4.addComponent(arg.tOpts,PREF,DEF,Short.MAX_VALUE);

			vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			/**/.addComponent(arg.tName)
			/**/.addComponent(arg.dType)
			/**/.addComponent(arg.tVal)
			/**/.addComponent(arg.tOpts));
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
	public void setAction(Action a)
		{
		plf.setMap(a.properties);
		for (int arg = 0; arg < Action.MAX_ARGS; arg++)
			args[arg].setComponents(a.arguments[arg]);
		}

	@Override
	public void stateChanged(ChangeEvent e)
		{
		recalculateArgsVisibility();
		}

	@Override
	public void actionPerformed(ActionEvent e)
		{
		recalculateArgsVisibility();
		}

	protected void recalculateArgsVisibility()
		{
		boolean showArgs = dKind.getSelectedItem() == InterfaceKind.NORMAL;
		int argNum = smArgNum.getNumber().intValue();
		lArgNum.setVisible(showArgs);
		sArgNum.setVisible(showArgs);
		for (int arg = 0; arg < Action.MAX_ARGS; arg++)
			args[arg].setVisible(showArgs && arg < argNum);
		}
	}
