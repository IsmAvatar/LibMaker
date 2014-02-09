/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.components;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.lateralgm.libmaker.Messages;

/**
 * A ListCellRenderer that additionally renders an enum
 * by polling Messages for key {prefix}{.name()}.
 * 
 * @author ismavatar
 */
public class EnumRenderer extends DefaultListCellRenderer
	{
	private static final long serialVersionUID = 1L;

	String prefix;

	public EnumRenderer(String prefix)
		{
		super();
		this.prefix = prefix;
		}

	public Component getListCellRendererComponent(JList<?> list, Object value, int index,
			boolean isSelected, boolean cellHasFocus)
		{
		if (value instanceof Enum<?>) value = Messages.getString(prefix + ((Enum<?>) value).name());
		return super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
		}
	}
