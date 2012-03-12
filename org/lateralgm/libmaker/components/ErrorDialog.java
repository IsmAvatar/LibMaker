/*
 * Copyright (C) 2011-2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2008 Clam <clamisgood@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.components;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.lateralgm.libmaker.Messages;

public class ErrorDialog extends JDialog implements ActionListener
	{
	private static final long serialVersionUID = 1L;
	private static final int DEBUG_HEIGHT = 200;

	protected JTextArea debugInfo;
	protected JButton copy;
	protected JButton ok;

	private static JButton makeButton(String key, ActionListener listener)
		{
		JButton but = new JButton(Messages.getString(key),Messages.getIconForKey(key));
		but.addActionListener(listener);
		return but;
		}

	public ErrorDialog(Frame parent, String title, String message, Throwable e)
		{
		this(parent,title,message,throwableToString(e));
		}

	public ErrorDialog(Frame parent, String title, String message, String debugInfo)
		{
		super(parent,title,true);
		setResizable(false);

		this.debugInfo = new JTextArea(debugInfo);
		JScrollPane scroll = new JScrollPane(this.debugInfo);

		Dimension dim = new Dimension(scroll.getWidth(),DEBUG_HEIGHT);
		scroll.setPreferredSize(dim);
		copy = makeButton("ErrorDialog.COPY",this); //$NON-NLS-1$
		ok = makeButton("ErrorDialog.OK",this); //$NON-NLS-1$
		dim = new Dimension(Math.max(copy.getPreferredSize().width,ok.getPreferredSize().width),
				copy.getPreferredSize().height);
		copy.setPreferredSize(dim);
		ok.setPreferredSize(dim);
		JOptionPane wtfwjd = new JOptionPane(new Object[] { message,scroll },JOptionPane.ERROR_MESSAGE,
				JOptionPane.DEFAULT_OPTION,null,new JButton[] { copy,ok });
		add(wtfwjd);
		pack();
		setLocationRelativeTo(parent);
		}

	protected static String throwableToString(Throwable e)
		{
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
		}

	public void actionPerformed(ActionEvent e)
		{
		if (e.getSource() == copy)
			{
			debugInfo.selectAll();
			debugInfo.copy();
			}
		else if (e.getSource() == ok) dispose();
		}
	}
