/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2009 Quadduc <quadduc@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.uilink;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonModel;

public class ButtonModelLink<K extends Enum<K>> extends PropertyLink<K,Boolean> implements
		ActionListener
	{
	public final ButtonModel model;

	public ButtonModelLink(ButtonModel bm, K k)
		{
		super(k);
		model = bm;
		//		reset();
		model.addActionListener(this);
		}

	public void actionPerformed(ActionEvent e)
		{
		boolean s = model.isSelected();
		if (Boolean.valueOf(s).equals(map.get(key))) return;
		editProperty(s);
		}

	protected void setComponent(Boolean t)
		{
		model.setSelected(t);
		}
	}
