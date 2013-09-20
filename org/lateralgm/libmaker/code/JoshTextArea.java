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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.lateralgm.joshedit.lexers.DefaultTokenMarker;
import org.lateralgm.joshedit.lexers.GMLTokenMarker;
import org.lateralgm.joshedit.Runner.JoshTextPanel;
import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.components.NumberField;
import org.lateralgm.main.LGM;

public class JoshTextArea extends JoshTextPanel
	{
	private static final long serialVersionUID = 1L;

	protected DefaultTokenMarker gmlTokenMarker = new GMLTokenMarker();

	JoshTextArea(String code)
		{
		super(code);

		//		setTabSize(Prefs.tabSize);
		setTokenMarker(gmlTokenMarker);
		setupKeywords();
		updateKeywords();
		//		text.setFont(Prefs.codeFont);
		//painter.setStyles(PrefsStore.getSyntaxStyles());
		//		text.getActionMap().put("COMPLETIONS",completionAction);
		//		LGM.currentFile.updateSource.addListener(this);
		}

	private JButton makeToolbarButton(String name)
	{
		String key = "JoshText." + name;
		JButton b = new JButton(LGM.getIconForKey(key));
		b.setToolTipText(Messages.getString(key));
		b.setRequestFocusEnabled(false);
		b.setActionCommand(key);
		//b.addActionListener(this);
		return b;
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

	private static JMenuItem makeContextButton(Action a)
	{
		String key = "JoshText." + a.getValue(Action.NAME);
		JMenuItem b = new JMenuItem(a);
		b.setIcon(LGM.getIconForKey(key));
		b.setText(Messages.getString(key));
		b.setRequestFocusEnabled(false);
		b.addActionListener(a);
		return b;
	}

	private void addEditorButtons(JToolBar tb)
		{
		tb.add(makeToolbarButton("SAVE"));
		tb.add(makeToolbarButton("LOAD"));
		tb.add(makeToolbarButton("PRINT"));
		tb.addSeparator();
		tb.add(makeToolbarButton("UNDO"));
		tb.add(makeToolbarButton("REDO"));
		tb.addSeparator();
		tb.add(makeToolbarButton("FIND"));
		tb.add(makeToolbarButton("GOTO"));
		tb.addSeparator();
		tb.add(makeToolbarButton("CUT"));
		tb.add(makeToolbarButton("COPY"));
		tb.add(makeToolbarButton("PASTE"));
		}

	AbstractAction gotoAction = new AbstractAction("GOTO")
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

	private void setupKeywords()
		{
		gmlTokenMarker.tmKeywords.add(GmlSyntax.functions);
		gmlTokenMarker.tmKeywords.add(GmlSyntax.constructs);
		gmlTokenMarker.tmKeywords.add(GmlSyntax.operators);
		gmlTokenMarker.tmKeywords.add(GmlSyntax.constants);
		gmlTokenMarker.tmKeywords.add(GmlSyntax.variables);
		}

	public static void updateKeywords()
		{
		GmlSyntax.constructs.words.clear();
		GmlSyntax.operators.words.clear();
		GmlSyntax.constants.words.clear();
		GmlSyntax.variables.words.clear();
		GmlSyntax.functions.words.clear();

		for (GmlSyntax.Construct keyword : GmlSyntax.CONSTRUCTS)
			GmlSyntax.constructs.words.add(keyword.getName());
		for (GmlSyntax.Operator keyword : GmlSyntax.OPERATORS)
			GmlSyntax.operators.words.add(keyword.getName());
		for (GmlSyntax.Constant keyword : GmlSyntax.CONSTANTS)
			GmlSyntax.constants.words.add(keyword.getName());
		for (GmlSyntax.Variable keyword : GmlSyntax.VARIABLES)
			GmlSyntax.variables.words.add(keyword.getName());
		for (GmlSyntax.Function keyword : GmlSyntax.FUNCTIONS)
			GmlSyntax.functions.words.add(keyword.getName());
		}

/*	protected void updateCompletions()
		{
		int l = 0;
		for (GmlSyntax.Keyword[] a : GmlSyntax.GML_KEYWORDS)
			l += a.length;
		completions = new Completion[l];
		int i = 0;
		for (GmlSyntax.Keyword[] a : GmlSyntax.GML_KEYWORDS)
			for (GmlSyntax.Keyword k : a)
				{
				if (k instanceof GmlSyntax.Function)
					completions[i] = new FunctionCompletion((GmlSyntax.Function) k);
				else if (k instanceof GmlSyntax.Variable)
					completions[i] = new VariableCompletion((GmlSyntax.Variable) k);
				else
					completions[i] = new CompletionMenu.WordCompletion(k.getName());
				i++;
				}
		}*/

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
		tool.add(makeToolbarButton(new AbstractAction("OK")
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

		//return
		if (code == null || code.equals(rc)) return null;
		return rc;
		}
	}
