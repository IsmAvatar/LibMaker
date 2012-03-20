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

package org.lateralgm.libmaker.code;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.lateralgm.joshedit.JoshText.CustomAction;
import org.lateralgm.joshedit.Runner.JoshTextPanel;
import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.components.NumberField;

public class JoshTextArea extends JoshTextPanel
	{
	private static final long serialVersionUID = 1L;

	JoshTextArea(String code)
		{
		super(code);
		}

	private static JButton makeToolbarButton(Action a)
		{
		String key = "JoshText." + a.getValue(Action.NAME);
		JButton b = new JButton(Messages.getIconForKey(key));
		b.setToolTipText(Messages.getString(key));
		b.setRequestFocusEnabled(false);
		b.addActionListener(a);
		return b;
		}

	private void addEditorButtons(JToolBar tb)
		{
		tb.add(makeToolbarButton(text.aUndo));
		tb.add(makeToolbarButton(text.aRedo));
		tb.add(makeToolbarButton(gotoAction));
		tb.addSeparator();
		tb.add(makeToolbarButton(text.aCut));
		tb.add(makeToolbarButton(text.aCopy));
		tb.add(makeToolbarButton(text.aPaste));
		}

	CustomAction gotoAction = new CustomAction("GOTO")
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
				{
				int line = showGotoDialog(getCaretLine());
				line = Math.max(0,Math.min(getLineCount() - 1,line));
				setCaretPosition(line,0);
				}
		};

	public static int showGotoDialog(int defVal)
		{
		final JDialog d = new JDialog((Frame) null,true);
		JPanel p = new JPanel();
		GroupLayout layout = new GroupLayout(p);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		p.setLayout(layout);

		JLabel l = new JLabel("Line: ");
		NumberField f = new NumberField(defVal);
		f.selectAll();
		JButton b = new JButton("Goto");
		b.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
					{
					d.setVisible(false);
					}
			});

		layout.setHorizontalGroup(layout.createParallelGroup()
		/**/.addGroup(layout.createSequentialGroup()
		/*	*/.addComponent(l)
		/*	*/.addComponent(f))
		/**/.addComponent(b,Alignment.CENTER));
		layout.setVerticalGroup(layout.createSequentialGroup()
		/**/.addGroup(layout.createParallelGroup()
		/*	*/.addComponent(l)
		/*	*/.addComponent(f))
		/**/.addComponent(b));

		//					JOptionPane.showMessageDialog(null,p);
		d.setContentPane(p);
		d.pack();
		d.setResizable(false);
		d.setLocationRelativeTo(null);
		d.setVisible(true); //blocks until user clicks OK

		return f.getIntValue();
		}

	static String rc;

	public static void returnCode(JDialog d, String code)
		{
		rc = code;
		d.setVisible(false);
		}

	public static String showInDialog(Frame frame, String title, final String code)
		{
		final JDialog d = new JDialog(frame,title,true);
		final JoshTextArea text = new JoshTextArea(code);

		JToolBar tool = new JToolBar();
		tool.setFloatable(false);
		tool.add(makeToolbarButton(new CustomAction("OK")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e)
					{
					returnCode(d,text.getTextCompat());
					}
			}));
		tool.addSeparator();
		text.addEditorButtons(tool);
		text.add(tool,BorderLayout.NORTH);

		d.add(new JScrollPane(text));

		d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		d.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent we)
					{
					String newCode = text.getTextCompat(); //guaranteed to be non-null
					if ((code == null && newCode.isEmpty()) || newCode.equals(code))
						{
						returnCode(d,null);
						return;
						}
					int r = JOptionPane.showConfirmDialog(d,"The code has changed. Save these changes? ");
					if (r == JOptionPane.YES_OPTION)
						returnCode(d,newCode);
					else if (r == JOptionPane.NO_OPTION) returnCode(d,null);
					}
			});
		d.pack();
		d.setLocationRelativeTo(null);
		d.setVisible(true);
		return rc;
		}
	}
