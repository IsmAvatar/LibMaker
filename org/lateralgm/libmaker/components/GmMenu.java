/*
 * Copyright (C) 2006, 2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2007 Quadduc <quadduc@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.components;

import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.lateralgm.libmaker.Messages;

public class GmMenu extends JMenu
	{
	private static final long serialVersionUID = 1L;
	protected ActionListener listener;

	public GmMenu(String s, ActionListener listener)
		{
		super();
		setTextAndAlt(this,s);
		this.listener = listener;
		}

	public JMenuItem addItem(String key)
		{
		return addItem(key,-1,-1);
		}

	public JMenuItem addItem(String key, int shortcut, int control)
		{
		JMenuItem item = new JMenuItem();
		if (key != null)
			{
			setTextAndAlt(item,Messages.getString(key));
			item.setIcon(Messages.getIconForKey(key));
			item.setActionCommand(key);
			}
		if (shortcut >= 0) item.setAccelerator(KeyStroke.getKeyStroke(shortcut,control));
		item.addActionListener(listener);
		add(item);
		return item;
		}

	public static final void setTextAndAlt(JMenuItem item, String input)
		{
		Matcher m = Pattern.compile("\t+([^\\s])$").matcher(input); //$NON-NLS-1$
		if (m.find())
			{
			int alt = m.group(1).toUpperCase(Locale.ENGLISH).charAt(0);
			item.setMnemonic(alt);
			item.setText(input.substring(0,m.start()));
			}
		else
			{
			item.setMnemonic(-1);
			item.setText(input);
			}
		}
	}
