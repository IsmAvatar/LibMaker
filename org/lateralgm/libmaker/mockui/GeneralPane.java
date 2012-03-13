/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.mockui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.backend.Action;
import org.lateralgm.libmaker.backend.Action.Execution;
import org.lateralgm.libmaker.backend.Action.Kind;
import org.lateralgm.libmaker.backend.Action.PAction;
import org.lateralgm.libmaker.components.EnumRenderer;
import org.lateralgm.libmaker.components.NumberField;
import org.lateralgm.libmaker.mockui.MockUI.ActionPanel;
import org.lateralgm.libmaker.mockui.MockUI.GroupPanel;
import org.lateralgm.libmaker.uilink.PropertyLink.PLFactory;

public class GeneralPane extends GroupPanel implements ActionPanel,ActionListener
	{
	private static final long serialVersionUID = 1L;

	NumberField tActId;
	JTextField tName, tDesc, tList, tHint, tFunction;
	JComboBox dKind, dExec;
	JCheckBox cbHidden, cbAdvanced, cbRegistered;
	JButton bImageChange, bExecCode;
	JLabel lExec, lFunc;

	Action a;

	PLFactory<PAction> plf;

	protected void initKeyComponents()
		{
		plf = new PLFactory<PAction>();

		tName = new JTextField();
		tActId = new NumberField(1,999);
		tDesc = new JTextField();
		tList = new JTextField();
		tHint = new JTextField();
		tFunction = new JTextField();
		plf.make(tName,PAction.NAME);
		plf.make(tActId,PAction.ID);
		plf.make(tDesc,PAction.DESCRIPTION);
		plf.make(tList,PAction.LIST);
		plf.make(tHint,PAction.HINT);
		plf.make(tFunction,PAction.EXEC_INFO);

		bImageChange = new JButton(Messages.getString("GeneralPane.IMAGE_CHANGE")); //$NON-NLS-1$
		bImageChange.addActionListener(this);

		String key = "GeneralPane.EXEC_CODE"; //$NON-NLS-1$
		bExecCode = new JButton(Messages.getString(key),Messages.getIconForKey(key));
		bExecCode.addActionListener(this);

		dKind = new JComboBox(Action.Kind.values());
		dKind.setRenderer(new EnumRenderer("ActionKind.")); //$NON-NLS-1$
		dKind.addActionListener(this);
		plf.make(dKind,PAction.KIND);

		dExec = new JComboBox(Action.Execution.values());
		dExec.setRenderer(new EnumRenderer("Execution.")); //$NON-NLS-1$
		dExec.addActionListener(this);
		plf.make(dExec,PAction.EXEC_TYPE);

		cbHidden = new JCheckBox(Messages.getString("GeneralPane.HIDDEN")); //$NON-NLS-1$
		cbAdvanced = new JCheckBox(Messages.getString("GeneralPane.ADVANCED")); //$NON-NLS-1$
		cbRegistered = new JCheckBox(Messages.getString("GeneralPane.PRO")); //$NON-NLS-1$
		plf.make(cbHidden,PAction.HIDDEN);
		plf.make(cbAdvanced,PAction.ADVANCED);
		plf.make(cbRegistered,PAction.REGISTERED);

		//These are key components so we can call .setVisible on them.
		lExec = new JLabel(Messages.getString("GeneralPane.EXECUTION")); //$NON-NLS-1$
		lFunc = new JLabel(Messages.getString("GeneralPane.FUNCTION")); //$NON-NLS-1$
		}

	@Override
	protected void layoutComponents(GroupLayout layout)
		{
		initKeyComponents();

		JLabel lName = new JLabel(Messages.getString("GeneralPane.NAME")); //$NON-NLS-1$
		JLabel lActId = new JLabel(Messages.getString("GeneralPane.ACT_ID")); //$NON-NLS-1$
		JLabel lImage = new JLabel(Messages.getString("GeneralPane.IMAGE")); //$NON-NLS-1$
		JLabel lKind = new JLabel(Messages.getString("GeneralPane.KIND")); //$NON-NLS-1$
		JLabel lDesc = new JLabel(Messages.getString("GeneralPane.DESCRIPTION")); //$NON-NLS-1$
		JLabel lList = new JLabel(Messages.getString("GeneralPane.LIST")); //$NON-NLS-1$
		JLabel lHint = new JLabel(Messages.getString("GeneralPane.HINT")); //$NON-NLS-1$

		JPanel imagePreview = new JPanel();
		imagePreview.setBorder(BorderFactory.createLoweredBevelBorder());
		imagePreview.setPreferredSize(new Dimension(32,32));

		int PREF = GroupLayout.PREFERRED_SIZE, DEF = GroupLayout.DEFAULT_SIZE;

		layout.setHorizontalGroup(layout.createSequentialGroup()
		/**/.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
		/*	*/.addComponent(lName)
		/*	*/.addComponent(lActId)
		/*	*/.addComponent(lImage)
		/*	*/.addComponent(lKind)
		/*	*/.addComponent(lExec))
		/**/.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
		/*	*/.addComponent(tName,GroupLayout.Alignment.TRAILING)
		/*	*/.addComponent(tActId,GroupLayout.Alignment.TRAILING)
		/*	*/.addGroup(layout.createSequentialGroup()
		/*		*/.addComponent(imagePreview,PREF,DEF,PREF)
		/*		*/.addComponent(bImageChange,DEF,93,Short.MAX_VALUE))
		/*	*/.addComponent(dKind,0,DEF,Short.MAX_VALUE)
		/*	*/.addComponent(dExec,0,DEF,Short.MAX_VALUE))
		/*	*/.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
		/*		*/.addComponent(lDesc)
		/*		*/.addComponent(lList)
		/*		*/.addComponent(lHint)
		/*		*/.addComponent(lFunc))
		/*	*/.addGroup(layout.createParallelGroup()
		/*		*/.addComponent(tDesc)
		/*		*/.addComponent(tList)
		/*		*/.addComponent(tHint)
		/*		*/.addGroup(layout.createSequentialGroup()
		/*			*/.addComponent(cbHidden)
		/*			*/.addComponent(cbAdvanced)
		/*			*/.addComponent(cbRegistered))
		/*		*/.addComponent(tFunction)
		/*		*/.addComponent(bExecCode)));
		layout.setVerticalGroup(layout.createSequentialGroup()
		/**/.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		/*	*/.addComponent(lName)
		/*	*/.addComponent(tName,PREF,DEF,PREF)
		/*	*/.addComponent(lDesc)
		/*	*/.addComponent(tDesc,PREF,DEF,PREF))
		/**/.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		/*	*/.addComponent(lActId)
		/*	*/.addComponent(tActId,PREF,DEF,PREF)
		/*	*/.addComponent(lList)
		/*	*/.addComponent(tList,PREF,DEF,PREF))
		/**/.addGroup(layout.createParallelGroup(/*not baseline*/)
		/*	*/.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		/*		*/.addComponent(lImage)
		/*		*/.addComponent(bImageChange)
		/*		*/.addComponent(lHint)
		/*		*/.addComponent(tHint,PREF,DEF,PREF))
		/*	*/.addComponent(imagePreview,PREF,DEF,PREF))
		/**/.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		/*	*/.addComponent(lKind)
		/*	*/.addComponent(dKind,PREF,DEF,PREF)
		/*	*/.addComponent(cbHidden)
		/*	*/.addComponent(cbAdvanced)
		/*	*/.addComponent(cbRegistered))
		/**/.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		/*	*/.addComponent(lExec)
		/*	*/.addComponent(dExec,PREF,DEF,PREF)
		/*	*/.addComponent(lFunc)
		/*	*/.addComponent(tFunction)
		/*	*/.addComponent(bExecCode)));
		}

	@Override
	public void setComponents(Action a)
		{
		this.a = a;
		plf.setMap(a.properties);

		/*tName.setText(a.getName());
		tActId.setText(Integer.toString(a.id));
		tDesc.setText(a.description);
		tList.setText(a.list);
		tHint.setText(a.hint);*/

		//		dKind.setSelectedItem(a.get(PAction.KIND));

		/*		cbHidden.setSelected(a.hidden);
				cbAdvanced.setSelected(a.advanced);
				cbRegistered.setSelected(a.registered);*/

		//		dExec.setSelectedItem(a.get(PAction.EXEC_TYPE));
		//		if (a.execType == Execution.FUNCTION) tFunction.setText(a.execInfo);
		}

	@Override
	public void actionPerformed(ActionEvent e)
		{
		Object src = e.getSource();
		if (src == dKind)
			{
			boolean normal = dKind.getSelectedItem() == Kind.NORMAL;
			if (normal ^ dExec.isVisible())
				{
				lExec.setVisible(normal);
				dExec.setVisible(normal);
				tFunction.setVisible(normal && dExec.getSelectedItem() == Execution.FUNCTION);
				lFunc.setVisible(tFunction.isVisible());
				bExecCode.setVisible(normal && dExec.getSelectedItem() == Execution.CODE);
				}
			return;
			}
		if (src == dExec)
			{
			boolean normal = dKind.getSelectedItem() == Kind.NORMAL;
			tFunction.setVisible(normal && dExec.getSelectedItem() == Execution.FUNCTION);
			lFunc.setVisible(tFunction.isVisible());
			bExecCode.setVisible(normal && dExec.getSelectedItem() == Execution.CODE);
			return;
			}

		//TODO: bImageChange and bExecCode
		}
	}
