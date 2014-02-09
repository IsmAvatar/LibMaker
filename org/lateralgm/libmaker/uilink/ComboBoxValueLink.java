/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.uilink;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class ComboBoxValueLink<K extends Enum<K>> extends PropertyLink<K,Object> implements
		ActionListener
	{
	JComboBox<?> combo;

	ComboBoxValueLink(JComboBox<?> cb, K k)
		{
		super(k);
		combo = cb;
		// reset();
		cb.addActionListener(this);
		}

	@Override
	protected void setComponent(Object t)
		{
		combo.setSelectedItem(t);
		}

	@Override
	public void actionPerformed(ActionEvent e)
		{
		editProperty(combo.getSelectedItem());
		}
	}
