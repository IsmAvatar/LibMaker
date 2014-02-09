/*
 * Copyright (C) 2007, 2009, 2010, 2011, 2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2007, 2008 Quadduc <quadduc@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.lateralgm.libmaker.backend.Action;
import org.lateralgm.libmaker.backend.Action.PAction;
import org.lateralgm.libmaker.backend.Argument;
import org.lateralgm.libmaker.backend.Argument.PArgument;

public class ActionPreview extends JInternalFrame implements ActionListener
	{
	private static final long serialVersionUID = 1L;

	private ButtonGroup applies;
	private JComboBox<String> appliesObject;
	private JPanel appliesPanel;
	private ArgumentComponent argComp[];
	private JCheckBox relativeBox;
	private JCheckBox notBox;
	private JButton save;
	private JButton discard;

	public static void showInFrame(Component parent, Action a)
		{
		final JFrame f = new JFrame();
		ActionPreview ap = new ActionPreview(a);
		ap.setVisible(true);
		ap.addInternalFrameListener(new InternalFrameAdapter()
			{
				public void internalFrameClosed(InternalFrameEvent e)
					{
					f.dispose();
					}
			});
		JDesktopPane dp = new JDesktopPane();
		dp.add(ap);
		f.add(dp);
		f.setSize(ap.getSize());
		f.setLocationRelativeTo(parent);
		f.setVisible(true);
		}

	public ActionPreview(Action a)
		{
		super((String) a.get(PAction.DESCRIPTION),false,true,false,false);
		BufferedImage img = a.get(PAction.IMAGE);
		if (img != null) setFrameIcon(new ImageIcon(img.getScaledInstance(16,16,Image.SCALE_SMOOTH)));

		String[] cao = {
				Messages.getString("ActionPreview.APPLY_OBJ_SELF"),Messages.getString("ActionPreview.APPLY_OBJ_EX") }; //$NON-NLS-1$ //$NON-NLS-2$
		appliesObject = new JComboBox<String>(cao);
		appliesObject.setEnabled(false);
		appliesObject.setOpaque(false);

		appliesPanel = new JPanel();
		appliesPanel.setOpaque(false);
		appliesPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc;
		applies = new ButtonGroup();
		JRadioButton button = new JRadioButton(Messages.getString("ActionPreview.APPLY_SELF"),true); //$NON-NLS-1$
		button.setOpaque(false);
		applies.add(button);
		gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		appliesPanel.add(button,gbc);
		button = new JRadioButton(Messages.getString("ActionPreview.APPLY_OTHER")); //$NON-NLS-1$
		button.setOpaque(false);
		applies.add(button);
		gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		appliesPanel.add(button,gbc);
		button = new JRadioButton(Messages.getString("ActionPreview.APPLY_OBJECT")); //$NON-NLS-1$
		button.setHorizontalAlignment(JRadioButton.LEFT);
		button.setOpaque(false);
		button.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
					{
					boolean sel = ((JRadioButton) e.getSource()).isSelected();
					appliesObject.setEnabled(sel);
					}
			});
		applies.add(button);
		gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		appliesPanel.add(button,gbc);
		gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0,2,0,6);
		appliesPanel.add(appliesObject,gbc);

		makeArgumentPane(a);
		pack();
		repaint();
		}

	public void makeArgumentPane(Action a)
		{
		setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		JLabel lab;
		JPanel pane;
		pane = new JPanel();
		pane.setBorder(new EmptyBorder(6,6,0,6));
		pane.setLayout(new BorderLayout());
		add(pane);
		BufferedImage img = a.get(PAction.IMAGE);
		if (img != null)
			{
			lab = new JLabel(new ImageIcon(img));
			lab.setBorder(new EmptyBorder(16,16,16,20));
			pane.add(lab,BorderLayout.LINE_START);
			}

		String s = Messages.getString("ActionPreview.APPLIES"); //$NON-NLS-1$
		appliesPanel.setBorder(BorderFactory.createTitledBorder(s));
		pane.add(appliesPanel);
		if (!(Boolean) a.get(PAction.APPLY)) appliesPanel.setVisible(false);

		argComp = new ArgumentComponent[a.get(PAction.ARG_NUM)];
		if (argComp.length > 0)
			{
			pane = new JPanel();
			pane.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(6,8,0,8),
					BorderFactory.createTitledBorder((String) null)));
			GroupLayout kvLayout = new GroupLayout(pane);
			GroupLayout.SequentialGroup hGroup, vGroup;
			GroupLayout.ParallelGroup keyGroup, valueGroup;
			hGroup = kvLayout.createSequentialGroup();
			vGroup = kvLayout.createSequentialGroup();
			keyGroup = kvLayout.createParallelGroup(Alignment.TRAILING);
			valueGroup = kvLayout.createParallelGroup();

			hGroup.addGap(4);
			hGroup.addGroup(keyGroup);
			hGroup.addGap(6);
			hGroup.addGroup(valueGroup);
			hGroup.addGap(4);

			kvLayout.setHorizontalGroup(hGroup);
			kvLayout.setVerticalGroup(vGroup);

			pane.setLayout(kvLayout);
			add(pane);

			vGroup.addGap(4);
			for (int n = 0; n < argComp.length; n++)
				{
				Argument arg = a.arguments[n];
				argComp[n] = new ArgumentComponent(arg);
				lab = new JLabel((String) arg.get(PArgument.CAPTION));
				Alignment al;
				if (n == 0 && a.get(PAction.IFACE_KIND) == Action.InterfaceKind.ARROWS)
					{
					argComp[n].setEditor(new ArrowsEditor((String) arg.get(PArgument.DEF_VALUE)));
					al = Alignment.CENTER;
					}
				else
					{
					Component c = argComp[n].getEditor();
					c.setMaximumSize(new Dimension(240,20));
					c.setPreferredSize(new Dimension(200,20));
					c.setMinimumSize(new Dimension(160,20));
					al = Alignment.BASELINE;
					}
				keyGroup.addComponent(lab);
				valueGroup.addComponent(argComp[n].getEditor());
				if (n > 0) vGroup.addGap(6);
				GroupLayout.ParallelGroup argGroup = kvLayout.createParallelGroup(al);
				argGroup.addComponent(lab).addComponent(argComp[n].getEditor());
				vGroup.addGroup(argGroup);
				}
			vGroup.addGap(4);
			}
		pane = new JPanel();
		pane.setLayout(new FlowLayout(FlowLayout.TRAILING));
		add(pane);
		if (a.get(PAction.RELATIVE))
			{
			relativeBox = new JCheckBox(Messages.getString("ActionPreview.RELATIVE")); //$NON-NLS-1$
			pane.add(relativeBox);
			}
		if (a.get(PAction.QUESTION))
			{
			notBox = new JCheckBox(Messages.getString("ActionPreview.NOT")); //$NON-NLS-1$
			pane.add(notBox);
			}

		pane = new JPanel();
		pane.setLayout(new GridLayout(1,2,8,0));
		pane.setBorder(new EmptyBorder(0,8,8,8));
		add(pane);
		s = Messages.getString("ActionPreview.SAVE"); //$NON-NLS-1$
		save = new JButton(s,Messages.getIconForKey("ActionPreview.SAVE")); //$NON-NLS-1$
		save.addActionListener(this);
		pane.add(save);
		s = Messages.getString("ActionPreview.DISCARD"); //$NON-NLS-1$
		discard = new JButton(s,Messages.getIconForKey("ActionPreview.DISCARD")); //$NON-NLS-1$
		discard.addActionListener(this);
		pane.add(discard);
		}

	private class ArrowsEditor extends JPanel
		{
		private static final long serialVersionUID = 1L;
		private JToggleButton[] arrows;
		private final Dimension btnSize = new Dimension(32,32);
		private final Dimension panelSize = new Dimension(96,96);

		public ArrowsEditor(String val)
			{
			setLayout(new GridLayout(3,3));
			arrows = new JToggleButton[9];
			BufferedImage icons = Messages.getImageForKey("ActionPreview.ARROWS"); //$NON-NLS-1$
			if (icons == null) icons = new BufferedImage(72,72,BufferedImage.TYPE_INT_ARGB);

			for (int i = 0; i < 9; i++)
				{
				arrows[i] = new JToggleButton();
				arrows[i].setIcon(new ImageIcon(icons.getSubimage(24 * (i % 3),24 * (i / 3),24,24)));
				arrows[i].setMinimumSize(btnSize);
				arrows[i].setPreferredSize(btnSize);
				int p = (2 - (i / 3)) * 3 + i % 3;
				if (val.length() > p) arrows[i].setSelected(val.charAt(p) == '1');
				add(arrows[i]);
				}
			setMaximumSize(panelSize);
			setPreferredSize(panelSize);
			}
		}

	private class ColorSelect extends JTextField
		{
		private static final long serialVersionUID = 1L;

		public ColorSelect(Color c)
			{
			super();
			setSelectedColor(c);
			}

		public void setSelectedColor(Color c)
			{
			setBackground(c);
			}
		}

	private class ArgumentComponent
		{
		private Argument arg;
		private Component editor;

		public ArgumentComponent(Argument arg)
			{
			this.arg = arg;
			editor = makeEditor();
			discard();
			}

		private JComponent makeEditor()
			{
			switch ((Argument.Kind) arg.get(PArgument.KIND))
				{
				case BOOLEAN:
					final String[] sab = { Messages.getString("ActionPreview.ARG_BOOL_FALSE"), //$NON-NLS-1$
							Messages.getString("ActionPreview.ARG_BOOL_TRUE") }; //$NON-NLS-1$
					return new JComboBox<String>(sab);
				case MENU:
					final String[] sam = ((String) arg.get(PArgument.MENU_OPTS)).split("\\|"); //$NON-NLS-1$
					return new JComboBox<String>(sam);
				case COLOR:
					return new ColorSelect(
							convertGmColor(Integer.parseInt((String) arg.get(PArgument.DEF_VALUE))));
				case SPRITE:
				case SOUND:
				case BACKGROUND:
				case PATH:
				case SCRIPT:
				case OBJECT:
				case ROOM:
				case FONT:
				case TIMELINE:
					final String[] sar = {
							Messages.getString("ActionPreview.ARG_RES_NONE"),Messages.getString("ActionPreview.ARG_RES_EX") }; //$NON-NLS-1$ //$NON-NLS-2$
					return new JComboBox<String>(sar);
				default:
					return new JTextField((String) arg.get(PArgument.DEF_VALUE));
				}
			}

		public Component getEditor()
			{
			return editor;
			}

		public void setEditor(Component editor)
			{
			this.editor = editor;
			}

		public void discard()
			{
			if (editor instanceof JComboBox)
				{
				try
					{
					int val = Integer.parseInt((String) arg.get(PArgument.DEF_VALUE));
					JComboBox<?> cb = (JComboBox<?>) editor;
					if (val < 0 || val >= cb.getItemCount()) val = 0;
					if (cb.getItemCount() == 0) val = -1;
					((JComboBox<?>) editor).setSelectedIndex(val);
					}
				catch (NumberFormatException e)
					{
					//fine, no selection then.
					}
				}
			}
		}

	public static Color convertGmColor(int col)
		{
		return new Color(col & 0xFF,(col & 0xFF00) >> 8,(col & 0xFF0000) >> 16);
		}

	public static String getKindName(Argument.Kind k)
		{
		return k.name();
		}

	public void actionPerformed(ActionEvent evt)
		{
		doDefaultCloseAction();
		}
	}
